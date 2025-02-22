package com.mns.todo.server;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {

    private String method;
    private String path;
    private String protocol;

    private Map<String, String> headers;
    private String body;
    private Map<String, String> params;

    public Request(String method, String path, String protocol) {
        this.method = method;

        var pathAndParams = path.split("\\?");
        this.path = pathAndParams[0];
        if (pathAndParams.length > 1) {
            this.params = Arrays.stream(pathAndParams[1].split("&"))
                    .map(s -> s.split("="))
                    .collect(Collectors.toMap(s -> s[0], s -> s[1]));
        }
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }


    public boolean hasParam(String key){
        return this.params != null && this.params.containsKey(key);
    }

    public String getParam(String key){
        if (!this.hasParam(key)) return null;
        return this.params.get(key);
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", protocol='" + protocol + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", params=" + params +
                '}';
    }
}
