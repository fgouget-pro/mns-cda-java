package com.mns.todo.server;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private String method;
    private String path;
    private String protocol;
    private Map<String, String> headers = new HashMap<String, String>();

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


    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", protocol='" + protocol + '\'' +
                ", headers=" + headers +
                '}';
    }
}
