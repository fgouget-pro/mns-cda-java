package com.mns.todo.server;

import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class Router {

    private final DatabaseAccess dba = DatabaseAccess.getInstance();

    public void route(Socket client, Request request) throws IOException {
        switch (request.getPath()) {
            case "/style.css":
                handleCss(client);
                break;
            case "/users":
                handleUsersPath(client);
                break;
            case "/users.json":
                handleUsersJson(client);
                break;
            case "/user":
                handleUserPath(client, request);
                break;
            case "/tasks":
                handleTasksPath(client);
                break;
            default:
                handle404(client);
                break;
        }
    }


    private void handleCss(Socket client) throws IOException {
        OutputStream outputStream = client.getOutputStream();
        try (FileInputStream fileInputStream = new FileInputStream("assets/style.css")) {
            sendFirstLines(outputStream, HttpStatusCode.FOUND, "text/css");
            outputStream.write(fileInputStream.readAllBytes());
            sendLastLinesAndFlush(outputStream);
        }
    }

    private void handleUserPath(Socket client, Request request) throws IOException {
        var userId = request.getParams().get("id");
        var user = dba.getUserById(Long.parseLong(userId));
        if (user == null) {
            handle404(client);
            return;
        }
        OutputStream outputStream = client.getOutputStream();
        sendFirstLines(outputStream, HttpStatusCode.FOUND);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body>");
        stringBuilder.append("<h1>User</h1>");
        stringBuilder.append("<p>").append(user.getFirstName()).append("</p>");
        stringBuilder.append("<p>").append(user.getLastName()).append("</p>");
        stringBuilder.append("</body></html>");

        var content = stringBuilder.toString();
        outputStream.write(content.getBytes());
        sendLastLinesAndFlush(outputStream);
    }

    private void sendLastLinesAndFlush(OutputStream outputStream) throws IOException {
        outputStream.write("\r\n\r\n".getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void sendFirstLines(OutputStream outputStream, HttpStatusCode statusCode, String contentType) throws IOException {
        outputStream.write(String.format("HTTP/1.1 %d %s\r\n",
                statusCode.getStatusCode(), statusCode.getMessage()).getBytes());
        outputStream.write(String.format("Content-Type: %s\r\n", contentType).getBytes());
        outputStream.write("\r\n".getBytes());
    }

    private void sendFirstLines(OutputStream outputStream, HttpStatusCode statusCode) throws IOException {
        sendFirstLines(outputStream, statusCode, "text/html");
    }

    private void handleTasksPath(Socket client) throws IOException {

    }

    private void handle404(Socket client) throws IOException {
        OutputStream outputStream = client.getOutputStream();
        sendFirstLines(outputStream, HttpStatusCode.NOT_FOUND);
        outputStream.write("<html><body><h1>404 Not Found</h1></body></html>".getBytes());
        sendLastLinesAndFlush(outputStream);
    }

    private void handleUsersJson(Socket client) throws IOException {
        OutputStream outputStream = client.getOutputStream();
        sendFirstLines(outputStream, HttpStatusCode.FOUND, "application/json");
        List<User> users = dba.getUsers();
        var jsonUsers = users.stream().map(user ->
                String.format("{\"id\": %d, \"firstName\": \"%s\", \"lastName\": \"%s\"}",
                        user.getId(), user.getFirstName(), user.getLastName())
        ).collect(Collectors.joining(",\r\n"));
        outputStream.write(("[\r\n" + jsonUsers + "\r\n]").getBytes());
        sendLastLinesAndFlush(outputStream);
    }


    private void handleUsersPath(Socket client) throws IOException {
        List<User> users = dba.getUsers();

        OutputStream outputStream = client.getOutputStream();
        sendFirstLines(outputStream, HttpStatusCode.FOUND);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html><body>");
        stringBuilder.append("<h1>Users:</h1>");
        stringBuilder.append("<ul>");
        for (User user : users) {
            String li = String.format("<li>%s %s (%d)</li>",
                    user.getFirstName(), user.getLastName(), user.getId());
            stringBuilder.append(li);
        }
        stringBuilder.append("</ul>");
        stringBuilder.append("</body></html>");

        var content = stringBuilder.toString();

        outputStream.write(content.getBytes());

        sendLastLinesAndFlush(outputStream);

    }

}
