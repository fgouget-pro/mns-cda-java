package com.mns.todo.server;

public enum HttpStatusCode {

    FOUND(200, "Found"),
    NOT_FOUND(404, "The specified resource could not be found"),
    UNAUTHORIZED(401, "Unauthorized"),
    INTERNAL_SERVER_ERROR(500, "Internal SERVER ERROR");

    private final int statusCode;
    private final String message;

    HttpStatusCode(int statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }


    public String getMessage() {
        return message;
    }

}
