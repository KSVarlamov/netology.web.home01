package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService poolExecutor = Executors.newFixedThreadPool(64);
    private final ConcurrentHashMap<String, Handler> getHandlers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Handler> posHandlers = new ConcurrentHashMap<>();

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final var socket = serverSocket.accept();
                    processConnection(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String page, Handler handler) {
        if ("GET".equals(method)) {
            getHandlers.put(page, handler);
            return;
        }
        if ("POST".equals(method)) {
            posHandlers.put(page, handler);
            return;
        }
        throw new IllegalArgumentException("Unknown method: " + method);
    }

    private void processConnection(Socket socket) {
        SimpleRequestMapper requestHandler = new SimpleRequestMapper(socket);
        poolExecutor.submit(requestHandler);
    }

    private class SimpleRequestMapper implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private BufferedOutputStream out;

        public SimpleRequestMapper(Socket socket) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new BufferedOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try (socket) {
                final var requestLine = in.readLine();
                final var parts = requestLine.split(" ");
                if (parts.length != 3) {
                    // just close socket
                    return;
                }
                Request request = new Request();
                request.setMethod(parts[0]).setPath(parts[1]).setProtocolType(parts[2]);
                //reading HTTP Headers
                while (in.ready()) {
                    String s = in.readLine();
                    if (s.length() == 0) {
                        break;
                    }
                    request.addHTTPHeader(s);
                }
                //reading request body
                while (in.ready()) {
                    String s = in.readLine();
                    request.addRequestBody(s);
                }
                processRequest(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processRequest(Request request) {
            switch (request.getMethod()) {
                case GET -> {
                    Handler handler = getHandlers.get(request.getPath());
                    if (handler != null) {
                        handler.handle(request, out);
                    } else {
                        response404();
                    }
                }
                case POST -> {
                    Handler handler = posHandlers.get(request.getPath());
                    if (handler != null) {
                        handler.handle(request, out);
                    } else {
                        response404();
                    }
                }
                default -> response404();
            }
        }

        private void response404() {
            try {
                out.write(("""
                        HTTP/1.1 404 Not Found\r
                        Content-Length: 0\r 
                        Connection: close\r 
                        \r"""
                ).getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
