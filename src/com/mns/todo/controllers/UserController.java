package com.mns.todo.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.ElementNotFoundException;
import com.mns.todo.model.User;
import com.mns.todo.server.HttpStatusCode;
import com.mns.todo.server.Request;
import com.mns.todo.server.RequestHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class UserController extends RequestHandler {

    private final DatabaseAccess dba = DatabaseAccess.getInstance();

    @Override
    public void handleGet(Request request, Socket client) throws IOException {
        String body = "";

        if (request.hasParam("id")) {
            body = buildSingleUserPage(Long.parseLong(request.getParam("id")));
        } else {
            body = buildBodyUsersList();
        }

        sendHttpStatus(client, HttpStatusCode.OK);
        sendContentType(client, "text/html");
        sendBody(client, body);
        sendLastLineAndFlush(client);
    }


    private String buildSingleUserPage(long id) {
        User user = null;
        try {
            user = dba.getUserById(id);
        } catch (ElementNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append(String.format("<title>%s %s</title>\n", user.getFirstName(), user.getLastName()));
        sb.append("</head></body>\n");
        sb.append("<a href=\"/users\">Retour</a>\n");
        sb.append("<form method=\"POST\">");
        sb.append(String.format("ID: %d <br>\n", user.getId()));
        sb.append(String.format("<label>First Name </label><input type=\"text\" name=\"firstName\" value=\"%s\"><br>\n", user.getFirstName()));
        sb.append(String.format("<label>Last Name </label><input type=\"text\" name=\"lastName\" value=\"%s\"><br>\n", user.getLastName()));
        sb.append("<button>OK</button>\n");
        sb.append("</form>\n");
        sb.append("</body></html>\n");
        return sb.toString();
    }

    private String buildBodyUsersList() {
        var users = dba.getUsers();
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>Liste des utilisateurs</title>\n");
        sb.append("</head></body>\n");
        sb.append("<h1> Liste des utilisateurs:</h1>\n");
        sb.append("<ul>\n");
        for (User user : users) {
            sb.append(String.format("\t<li><a href=\"users?id=%d\"> %d %s %s</a></li>\n", user.getId(), user.getId(), user.getFirstName(), user.getLastName()));
        }
        sb.append("</ul>\n</body>\n</html>\n");
        return sb.toString();
    }

    @Override
    public void handlePost(Request request, Socket client) throws IOException {
        String body = request.getBody();
        if (body == null || body.isEmpty()) {
            sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
            sendLastLineAndFlush(client);
        }
        switch (request.getHeaders().get("Content-Type")){
            case "application/json":
                ObjectMapper mapper = new ObjectMapper();
                try {
                    User u = mapper.readValue(body, User.class);
                    dba.addUser(u);
                } catch (JsonMappingException e) {
                    sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
                    sendLastLineAndFlush(client);
                }
                sendHttpStatus(client, HttpStatusCode.CREATED);
                break;
            case "application/x-www-form-urlencoded":
                if (body == null || body.isEmpty()) {
                    sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
                    sendLastLineAndFlush(client);
                }
                User u = new User();
                u.setId(Long.parseLong(request.getParam("id")));
                Arrays.stream(body.split("&")).forEach(s -> {
                    String[] keyValue = s.split("=");
                    switch (keyValue[0]) {
                        case "firstName":
                            u.setFirstName(keyValue[1]);
                            break;
                        case "lastName":
                            u.setLastName(keyValue[1]);
                            break;
                    }
                });
                dba.saveUser(u);
                handleGet(request, client);
        }
    }


    public void handlePut(Request request, Socket client) throws IOException {


    }
}
