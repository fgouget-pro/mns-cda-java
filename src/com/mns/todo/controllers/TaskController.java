package com.mns.todo.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fgouget.framework.server.annotations.Controller;
import com.fgouget.framework.server.annotations.HANDLE;
import com.fgouget.framework.server.enums.HttpMethods;
import com.fgouget.framework.server.model.Response;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.ElementNotFoundException;
import com.mns.todo.model.Task;
import com.fgouget.framework.server.enums.HttpStatusCode;
import com.fgouget.framework.server.model.Request;
import com.fgouget.framework.server.RequestHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Controller(route="/tasks")
public class TaskController extends RequestHandler {
    private final DatabaseAccess dba;

    public TaskController(DatabaseAccess databaseAccess) {
        dba = databaseAccess;
    }

    @HANDLE
    public String handleGet(Request request, Response response) throws IOException {
        String body;
        if (request.hasParam("id")) {
            Task task;
            try {
                task = dba.getTaskByID(Long.parseLong(request.getParam("id")));
            } catch (ElementNotFoundException e) {
                throw new RuntimeException(e);
            }
            response.getContext().put("task", task);
            return "task";
        } else {
            var done = "all";
            List<Task> tasks;
            if (request.hasParam("done")) {
                done = request.getParam("done");
                tasks = dba.getTasks(Boolean.parseBoolean(done));
            } else {
                tasks = dba.getTasks();
            }
            response.getContext().put("tasks", tasks);
            response.getContext().put("done", done);

            return "tasks";
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
                    Task t = mapper.readValue(body, Task.class);
                    dba.addTask(t);
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
                Task t = new Task();
                t.setId(Long.parseLong(request.getParam("id")));
                Arrays.stream(body.split("&")).forEach(s -> {
                    String[] keyValue = s.split("=");
                    String value = "";
                    if (keyValue.length == 2) {
                        value = keyValue[1].replace("+", " ");
                    }
                    switch (keyValue[0]) {
                        case "title":
                            t.setTitle(value);
                            break;
                        case "description":
                            t.setDescription(value);
                            break;
                        case "done":
                            t.setDone("on".equals(value));
                            break;
                    }
                });
                dba.saveTask(t);
        }
    }
}
