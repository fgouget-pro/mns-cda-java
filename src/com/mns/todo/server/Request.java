package com.mns.todo.server;

import java.util.Map;

public class Request {

    private String method;
    private String path;
    private String protocol;

    private Map<String, String> headers;
    private String body;


    public Request(String method, String path, String protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + headers +
                ", protocol='" + protocol + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
