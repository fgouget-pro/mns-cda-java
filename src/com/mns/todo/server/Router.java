package com.mns.todo.server;

import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.model.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class Router {

    private final DatabaseAccess dba = DatabaseAccess.getInstance();

    public void route(Socket client, Request request) throws IOException {
        switch (request.getPath()){
            case "/users":
                handleUsersPath(client);
                break;
            default:
                handle404(client);
                break;
        }
    }

    private void handle404(Socket client) throws IOException {
        OutputStream outputStream = client.getOutputStream();
        outputStream.write("HTTP/1.1 404\r\n".getBytes());
        outputStream.write("Content-Type: text/html\r\n".getBytes());
        outputStream.write("\r\n".getBytes());
        outputStream.write("<html><body><h1>404 Not Found</h1></body></html>".getBytes());
        outputStream.write("\r\n\r\n".getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void handleUsersPath(Socket client) throws IOException {
        List<User> users = dba.getUsers();

        OutputStream outputStream = client.getOutputStream();
        outputStream.write("HTTP/1.1 200\r\n".getBytes());
        outputStream.write("Content-Type: text/html\r\n".getBytes());
        outputStream.write("\r\n".getBytes());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><body>");
        stringBuilder.append("<h1>Users:</h1>");
        stringBuilder.append("<ul>");
        for (User user: users){
            String li = String.format("<li>%s %s (%d)</li>",
                    user.getFirstName(), user.getLastName(), user.getId());
            stringBuilder.append(li);
        }
        stringBuilder.append("</ul>");
        stringBuilder.append("</body></html>");

        var content = stringBuilder.toString();

        outputStream.write(content.getBytes());

        outputStream.write("\r\n\r\n".getBytes());
        outputStream.flush();
        outputStream.close();

    }

}
