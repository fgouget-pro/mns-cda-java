package com.mns.todo.server;

import java.io.IOException;
import java.net.Socket;


public abstract class RequestHandler {


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
        sendContentType(client,"text/html");
        sendBody(client, "<b>Not Implemented</b>");
        sendLastLineAndFlush(client);
    }

    public abstract void handleGet(Request request, Socket client) throws IOException;


    public void handlePost(Request request, Socket client) throws IOException {
        sent501(client);
    }

    public void handlePut(Request request, Socket client) throws IOException {
        sent501(client);
    }

    public void handleDelete(Request request, Socket client) throws IOException {
        sent501(client);
    }


    public void handle(Request request, Socket client) throws IOException {
        System.out.println(">>" + request.getMethod()+ "<<");
        System.out.println(this.getClass());
        switch (request.getMethod()) {
            case "POST":
                handlePost(request, client);
                break;
            case "PUT":
                handlePut(request, client);
                break;
            case "DELETE":
                handleDelete(request, client);
                break;
            default:
                handleGet(request, client);
        }
    }

}
