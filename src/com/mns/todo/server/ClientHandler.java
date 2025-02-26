package com.mns.todo.server;

import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler {


    public void handle(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        Request request = new Request();

        String firstLine = reader.readLine();
        String[] splitFirstLine = firstLine.split(" ");

        request.setMethod(splitFirstLine[0]);
        request.setPath(splitFirstLine[1]);
        request.setProtocol(splitFirstLine[2]);
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            var h = line.split(": ");
            request.getHeaders().put(h[0], h[1]);
        }

        new Router().route(client, request);

        reader.close();
        client.close();
    }



}
