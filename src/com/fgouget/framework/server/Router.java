package com.fgouget.framework.server;

import com.fgouget.framework.server.annotations.HANDLE;
import com.fgouget.framework.server.enums.HttpStatusCode;
import com.fgouget.framework.server.model.Request;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class Router implements IRouter {

    private final Map<String, IRequestHandler> handlers = new HashMap<>();

    private RequestHandler defaultHandler = new RequestHandler() {
        @HANDLE()
        public void handleGet(Request request) throws IOException {
            Socket client = request.getClient();
            sendHttpStatus(client, HttpStatusCode.NOT_FOUND);
            sendContentType(client,"text/html");
            sendBody(client, "<b>Not Found</b>");
            sendLastLineAndFlush(client);
        }
    };

    public ClientHandler getClientHandler(Socket client){
        return new ClientHandler(client, this);
    }

    protected void handleRouting(Request req) throws IOException {
        System.out.println(handlers);
        if (handlers.containsKey(req.getPath())){
            handlers.get(req.getPath()).handle(req);
        } else {
            defaultHandler.handle(req);
        }
    }

    public void addHandler(String path, IRequestHandler handler){
        System.out.println("ADDING HANDLER FOR PATH: " + path);
        handlers.put(path, handler);
    }

    public RequestHandler getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(RequestHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

}
