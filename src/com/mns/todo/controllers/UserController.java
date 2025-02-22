package com.mns.todo.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.model.User;
import com.mns.todo.server.HttpStatusCode;
import com.mns.todo.server.Request;
import com.mns.todo.server.RequestHandler;

import java.io.IOException;
import java.net.Socket;

public class UserController extends RequestHandler {

    private final DatabaseAccess dba = DatabaseAccess.getInstance();

    @Override
    public void handleGet(Request request, Socket client) throws IOException {
        var users = dba.getUsers();
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>Liste des utilisateurs</title>\n");
        sb.append("</head></body>\n");
        sb.append("<h1> Liste des utilisateurs:</h1>\n");
        sb.append("<ul>\n");
        for (User user : users) {
            sb.append(String.format("\t<li>%d %s %s</li>\n", user.getId(), user.getFirstName(), user.getLastName()));
        }
        sb.append("</ul>\n</body>\n</html>\n");

        sendHttpStatus(client, HttpStatusCode.OK);
        sendContentType(client, "text/html");
        sendBody(client, sb.toString());
        sendLastLineAndFlush(client);
    }

    @Override
    public void handlePost(Request request, Socket client) throws IOException {
        System.out.println("Inside UserController.handlePost");
        String body = request.getBody();
        if (body == null || body.isEmpty()) {
            sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
            sendLastLineAndFlush(client);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            User u = mapper.readValue(body, User.class);
            dba.addUser(u);
        } catch (JsonMappingException e) {
            sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
            sendLastLineAndFlush(client);
        }
        sendHttpStatus(client, HttpStatusCode.CREATED);
    }
}
