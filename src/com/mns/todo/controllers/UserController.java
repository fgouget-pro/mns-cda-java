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

    private StringBuilder createHtmlHeader(String title){
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append(String.format("<title>%s</title>\n", title));
        html.append("<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH\" crossorigin=\"anonymous\">");
        html.append("</head></body><div class=\"container-fluid\">\n");
        return html;
    }

    private StringBuilder createHtmlFooter(StringBuilder sb){
        return sb.append("</div></body></html>\n");
    }

    private String createInputLine(String label, String name, String value){
        return String.format("<label class=\"form-label\">%s </label><input class=\"form-control\" type=\"text\" name=\"%s\" value=\"%s\"><br>\n",label,name, value);
    }

    private String buildSingleUserPage(long id) {
        User user;
        try {
            user = dba.getUserById(id);
        } catch (ElementNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = createHtmlHeader(user.getFirstName() + " " + user.getLastName());
        sb.append("<a href=\"/users\">Retour</a>\n");
        sb.append("<form method=\"POST\">");
        sb.append(String.format("ID: %d <br>\n", user.getId()));
        sb.append(createInputLine("First Name", "firstName", user.getFirstName()));
        sb.append(createInputLine("Last Name", "lastName", user.getLastName()));
        sb.append("<button class=\"btn btn-success\">OK</button>\n");
        sb.append("</form>\n");
        return createHtmlFooter(sb).toString();
    }

    private String buildBodyUsersList() {
        var users = dba.getUsers();
        StringBuilder sb = createHtmlHeader("Liste des utilisateurs");
        sb.append("<h1> Liste des utilisateurs:</h1>\n");
        sb.append("<ul>\n");
        for (User user : users) {
            sb.append(String.format("\t<li><a href=\"users?id=%d\"> %d %s %s</a></li>\n", user.getId(), user.getId(), user.getFirstName(), user.getLastName()));
        }
        sb.append("</ul>\n");
        return createHtmlFooter(sb).toString();
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
                    sendHttpStatus(client, HttpStatusCode.CREATED);
                } catch (JsonMappingException e) {
                    sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
                    sendLastLineAndFlush(client);
                }
                break;
            case "application/x-www-form-urlencoded":
                if (body == null || body.isEmpty()) {
                    sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
                    sendLastLineAndFlush(client);
                    return;
                }
                User u = new User();
                u.setId(Long.parseLong(request.getParam("id")));
                Arrays.stream(body.split("&")).forEach(s -> {
                    String[] keyValue = s.split("=");
                    switch (keyValue[0]) {
                        case "firstName":
                            if (keyValue.length == 2) {
                                u.setFirstName(keyValue[1]);
                            } else {
                                u.setFirstName("");
                            }
                            break;
                        case "lastName":
                            if (keyValue.length == 2) {
                                u.setLastName(keyValue[1]);
                            } else {
                                u.setLastName("");
                            }
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
