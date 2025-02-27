package com.mns.todo.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {

    private String method;
    private String path;
    private String protocol;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> params;

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
        // path = /real/path/xxx/yyy?param1=value&param2=value2
        var realPath = path;
        if (path.contains("?")) {
            var split = path.split("\\?"); // ["/real/path/xxx/yyy", "param1=value&param2=value2"]
            realPath = split[0]; // realPath = "/real/path/xxx/yyy"
            params = Arrays
                    .stream(split[1].split("&")) //  {"param1=value", "param2=value2"}
                    .map(s -> s.split("=")) // {["param1","value"], ["param2", "value2"]}
                    .collect(Collectors.toMap(
                                    arr -> arr[0], // ["param1", "value"] -> "param1"
                                    arr -> arr[1])); // ["param1", "value"] -> "value"
            // realPath = "/real/path/xxx/yyy"
            // params = {"param1"="value","param2"="value2"}
            System.out.println(params);
        }
        this.path = realPath;
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

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public boolean hasHeader(String key) {
        return this.headers.containsKey(key);
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

    public Map<String, String> getParams() {
        if (params == null) {return new HashMap<>();}
        return params;
    }
}
