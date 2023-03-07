package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private final Map<String, String> headers = new HashMap<>();
    private Method method = Method.GET;
    private URI uri;
    private String protocol;

    private List<NameValuePair> getValues = new ArrayList<>();
    private List<NameValuePair> postValues = new ArrayList<>();


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
            getValues = URLEncodedUtils.parse(this.uri, StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    public List<String> getQueryParam(String name) {
        List<String> result = new ArrayList<>();
        for (NameValuePair pair : getValues) {
            if (pair.getName().equals(name)) {
                result.add(pair.getValue());
            }
        }
        return result;
    }

    public List<NameValuePair> getQueryValues() {
        return this.getValues;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }


    public void setBody(char[] body) {
        String contentType = headers.get("Content-Type");
        if (contentType.equals("application/x-www-form-urlencoded")) {
            String[] parts = new String(body).split("&");
            postValues = new ArrayList<>();
            for (String part : parts) {
                String[] tmp = part.split("=");
                if (tmp.length != 2) {
                    continue;
                }
                NameValuePair pair = new BasicNameValuePair(tmp[0], tmp[1]);
                postValues.add(pair);
            }
        }
    }

    public List<NameValuePair> getPostValues() {
        return postValues;
    }

    public List<String> getPostParam(String name) {
        List<String> result = new ArrayList<>();
        for (NameValuePair pair : postValues) {
            if (pair.getName().equals(name)) {
                result.add(pair.getValue());
            }
        }
        return result;
    }

    enum Method {
        GET, POST
    }
}

