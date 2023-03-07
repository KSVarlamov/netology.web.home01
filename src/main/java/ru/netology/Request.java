package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private final Map<String, String> headers = new HashMap<>();
    private final List<String> body = new ArrayList<>();
    private Method method = Method.GET;
    private URI uri;
    private String protocol;

    private List<NameValuePair> GETValues;

    public Request setProtocolType(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public Request addHTTPHeader(String s) {
        String key = s.substring(0, s.indexOf(": "));
        String value = s.substring(s.indexOf(": ") + 1).trim();
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
        return uri.getPath();
    }

    public Request setURI(String uri) {
        try {
            this.uri = new URI(uri);
            GETValues = URLEncodedUtils.parse(this.uri, StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    public List<String> getQueryParam(String name) {
        List<String> result = new ArrayList<>();
        for (NameValuePair pair : GETValues) {
            if (pair.getName().equals(name)) {
                result.add(pair.getValue());
            }
        }
        return result;
    }
    public List<NameValuePair> getQueryParams() {
        return this.GETValues;
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

