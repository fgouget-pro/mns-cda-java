package com.fgouget.framework.server;

import com.fgouget.framework.util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements IServer{

    private final IRouter router;
    private final Logger logger;

    public Server(IRouter router, Logger logger) {
        this.router = router;
        this.logger = logger;
    }

    public void serve(int port){
        try (ExecutorService service = Executors.newVirtualThreadPerTaskExecutor()) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                logger.log("Server started on port " + port);
                while (true) {
                    logger.log("Waiting for connection...");
                    try {
                        Socket client = serverSocket.accept();
                        service.submit(() -> router.getClientHandler(client).handleClient());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
