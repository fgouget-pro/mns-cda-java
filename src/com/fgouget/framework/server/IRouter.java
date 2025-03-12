package com.fgouget.framework.server;


import javax.inject.Named;
import java.net.Socket;

@Named
public interface IRouter {

    ClientHandler getClientHandler(Socket client);

    void addHandler(String path, RequestHandler handler);

    RequestHandler getDefaultHandler();

    void setDefaultHandler(RequestHandler defaultHandler);

}
