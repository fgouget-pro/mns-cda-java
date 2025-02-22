package com.mns.todo.server;

public enum HttpStatusCode {
    NOT_FOUND(404, "Not Found"),
    OK(200, "OK"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_REQUEST(400, "Bad Request"),
    CREATED(201, "Created"),;


    private int code;
    private String message;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
