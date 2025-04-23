package com.mns.todo.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgouget.framework.server.RequestHandler;
import com.fgouget.framework.server.annotations.Controller;
import com.fgouget.framework.server.annotations.HANDLE;
import com.fgouget.framework.server.enums.HttpMethods;
import com.fgouget.framework.server.enums.HttpStatusCode;
import com.fgouget.framework.server.model.Request;
import com.fgouget.framework.server.model.Response;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.ElementNotFoundException;
import com.mns.todo.model.User;

import java.io.IOException;
import java.util.Arrays;

@Controller(route = "/users")
public class UserController extends RequestHandler {

    private final DatabaseAccess dba;

    public UserController(DatabaseAccess dba) {
        this.dba = dba;
    }

    @HANDLE
    public String handleGet(Request request, Response response) {
        if (request.hasParam("id")) {
            try {
                var user = dba.getUserById(Long.parseLong(request.getParam("id")));
                response.getContext().put("user", user);
                return "user";
            } catch (ElementNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            var users = dba.getUsers();
            response.getContext().put("users", users);
            return "users";
        }
    }


    @HANDLE(httpMethod = HttpMethods.POST)
    public void handlePost(Request request) throws IOException {
        String body = request.getBody();
        var client = request.getClient();
        if (body == null || body.isEmpty()) {
            sendHttpStatus(client, HttpStatusCode.BAD_REQUEST);
            sendLastLineAndFlush(client);
        }
        switch (request.getHeaders().get("Content-Type")) {
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
                //handleGet(request, null);
        }
    }

}
