package ru.netology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private final Map<String, String> headers = new HashMap<>();
    private Method method = Method.GET;
    private String path;
    private String protocol;
    private final List<String> body = new ArrayList<>();

    public Request setProtocolType(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public Request addHTTPHeader(String s) {
        String key = s.substring(0, s.indexOf(':'));
        String value = s.substring(s.indexOf(':') + 1).trim();
        headers.put(key, value);
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public Request setMethod(String part) {
        switch (part) {
            case "GET" -> method = Method.GET;
            case "POST" -> method = Method.POST;
            default -> throw new IllegalArgumentException("Unknown method " + part);
        }
        return this;
    }

    public String getPath() {
        return path;
    }

    public Request setPath(String path) {
        this.path = path;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<String> getBody() {
        return body;
    }

    public void addRequestBody(String s) {
        body.add(s);
    }

    enum Method {
        GET, POST
    }
}

