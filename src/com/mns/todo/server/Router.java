package com.mns.todo.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Router {

    private final Map<String, RequestHandler> handlers = new HashMap<>();

    private RequestHandler defaultHandler = new RequestHandler() {
        @Override
        public void handleGet(Request request, Socket client) throws IOException {
            sendHttpStatus(client, HttpStatusCode.NOT_FOUND);
            sendContentType(client,"text/html");
            sendBody(client, "<b>Not Found</b>");
            sendLastLineAndFlush(client);
        }
    };

    public ClientHandler getClientHandler(Socket client){
        System.out.println("GET CLIENT HANDLER FROM THREAD " + Thread.currentThread().getName());
        System.out.println(client.isClosed());
        return new ClientHandler(client, this);
    }

    protected void handleRouting(Request req, Socket client) throws IOException {
        if (handlers.containsKey(req.getPath())){
            handlers.get(req.getPath()).handle(req, client);
        } else {
            defaultHandler.handle(req, client);
        }
    }

    public void addHandler(String path, RequestHandler handler){
        handlers.put(path, handler);
    }

    public RequestHandler getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(RequestHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

}
