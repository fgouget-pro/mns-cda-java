package com.fgouget.framework.server;

import com.fgouget.framework.server.enums.HttpStatusCode;
import com.fgouget.framework.server.model.Request;
import com.fgouget.framework.server.model.Response;

import java.io.IOException;
import java.net.Socket;

public interface IRequestHandler {

    void sendHttpStatus(Socket client, HttpStatusCode statusCode) throws IOException;

    void sendHeader(Socket client, String headerName, String headerValue) throws IOException;

    void sendBody(Socket client, String body) throws IOException;

    void sendLastLineAndFlush(Socket client) throws IOException;

    void sendContentType(Socket client, String contentType) throws IOException;

    void handle(Request request) throws IOException;

    String getRoute();

    void sentWithResponse(Request request, Response response) throws IOException;

}
