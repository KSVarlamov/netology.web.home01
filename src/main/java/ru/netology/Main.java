package ru.netology;

import org.apache.hc.core5.http.NameValuePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    private static final List<String> simplePages = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/events.html", "/events.js", "/default-post.html");

    public static void main(String[] args) {
        Server server = new Server();

        for (String s : simplePages) {
            server.addHandler("GET", s, (request, out) -> {
                try {
                    final var filePath = Path.of(".", "public", request.getPath());
                    final var mimeType = Files.probeContentType(filePath);
                    final var length = Files.size(filePath);
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    Files.copy(filePath, out);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        server.addHandler("GET", "/classic.html", (request, out) -> {
            try {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("GET", "/default-get.html", (request, out) -> {
            try {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                final var template = Files.readString(filePath);
                final var content = template.getBytes();
                if (!request.getQueryValues().isEmpty()) {
                    for (String str : request.getQueryParam("value")) {
                        System.out.println("value: " + str);
                    }
                }
                if (!request.getQueryValues().isEmpty()) {
                    for (String str : request.getQueryParam("image")) {
                        System.out.println("image: " + str);
                    }
                }
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.addHandler("POST", "/", (request, out) -> {

            if (!request.getPostValues().isEmpty()) {
                System.out.println("POST Values:");
                for (NameValuePair pair : request.getPostValues()) {
                    System.out.printf("%s = [%s]%n", pair.getName(), pair.getValue());
                }
            }
            if (!request.getQueryValues().isEmpty()) {
                System.out.println("GET Values:");
                for (NameValuePair pair : request.getQueryValues()) {
                    System.out.printf("%s = [%s]%n", pair.getName(), pair.getValue());
                }
            }

            try {
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.start(9999);
    }

}


