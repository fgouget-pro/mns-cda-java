package com.fgouget.framework.server.model;

import com.fgouget.framework.server.enums.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private HttpStatusCode httpStatusCode = HttpStatusCode.OK;
    private String contentType = "text/html; charset=utf-8";
    private String body = "";

    public Map<String, Object> getContext() {
        if (this.context == null){
            this.context = new HashMap<String, Object>();
        }
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    private Map<String, Object> context;


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
