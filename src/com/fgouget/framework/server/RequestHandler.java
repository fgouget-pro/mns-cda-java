package com.fgouget.framework.server;

import com.fgouget.framework.server.enums.HttpStatusCode;
import com.fgouget.framework.server.model.Request;
import com.fgouget.framework.server.model.Response;

import java.io.IOException;
import java.net.Socket;


public abstract class RequestHandler implements IRequestHandler {

    public void sendHttpStatus(Socket client, HttpStatusCode statusCode) throws IOException {
        var firstLine = String.format("HTTP/1.1 %d %s\r\n", statusCode.getCode(), statusCode.getMessage());
        client.getOutputStream().write(firstLine.getBytes());
    }

    public void sendHeader(Socket client, String headerName, String headerValue) throws IOException {
        var line = String.format("%s: %s\r\n", headerName, headerValue);
        client.getOutputStream().write(line.getBytes());
    }

    public void sendBody(Socket client, String body) throws IOException {
        var os = client.getOutputStream();
        os.write("\r\n".getBytes());
        os.write(body.getBytes());
    }

    public void sendLastLineAndFlush(Socket client) throws IOException {
        client.getOutputStream().write("\r\n\r\n".getBytes());
        client.getOutputStream().flush();
    }

    public void sendContentType(Socket client, String contentType) throws IOException {
        sendHeader(client, "Content-Type", contentType);
    }

    protected void sent501(Socket client) throws IOException {
        sendHttpStatus(client, HttpStatusCode.NOT_IMPLEMENTED);
        sendContentType(client, "text/html");
        sendBody(client, "<b>Not Implemented</b>");
        sendLastLineAndFlush(client);
    }

    protected void sent401(Socket client) throws IOException {
        sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
        sendContentType(client, "text/html");
        sendBody(client, "<b>Bad Request</b>");
        sendLastLineAndFlush(client);
    }


    public final String getRoute(){
        return "";
    }

    @Override
    public void handle(Request request) throws IOException {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public final void sentWithResponse(Request req, Response response) throws IOException {
        var client = req.getClient();
        sendHttpStatus(client, response.getHttpStatusCode());
        sendContentType(client, response.getContentType());
        sendBody(client, response.getBody());
        sendLastLineAndFlush(client);
    }
}
