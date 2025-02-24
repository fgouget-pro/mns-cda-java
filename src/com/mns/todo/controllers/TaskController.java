package com.mns.todo.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.ElementNotFoundException;
import com.mns.todo.model.Task;
import com.mns.todo.server.HttpStatusCode;
import com.mns.todo.server.Request;
import com.mns.todo.server.RequestHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class TaskController extends RequestHandler {
    private final DatabaseAccess dba = DatabaseAccess.getInstance();

    @Override
    public void handleGet(Request request, Socket client) throws IOException {
        String body = "";
        if (request.hasParam("id")) {
            body = buildSingleTaskPage(Long.parseLong(request.getParam("id")));
        } else {
            var done = "all";
            if (request.hasParam("done")) {
                done = request.getParam("done");
            }
            body = buildBodyTasksList(done);
        }
        sendHttpStatus(client, HttpStatusCode.OK);
        sendContentType(client, "text/html");
        sendBody(client, body);
        sendLastLineAndFlush(client);
    }


    private String buildBodyTasksList(String done) {

        List<Task> tasks;
        if ("all".equals(done)) {
            tasks = dba.getTasks();
        } else {
            tasks = dba.getTasks(Boolean.parseBoolean(done));
        }

        StringBuilder sb = createHtmlHeader("Liste des taches");
        sb.append("<h1>Liste des taches:</h1>\n");
        if (!done.equals("true")) {
            sb.append(makeLink("/tasks?done=true", "Taches Terminees"))
                    .append("&nbsp;");

        }
        if (!done.equals("false")) {
            sb.append(makeLink("/tasks?done=false", "Taches en cours"))
                    .append("&nbsp;");
        }
        if (!done.equals("all")) {
            sb.append(makeLink("/tasks", "Toutes les tachee"));
        }
        sb.append("\n<br>\n");
        sb.append("<ul>\n");
        for (Task task : tasks) {
            sb.append(String.format("<li>%s</li>",
                    makeLink("tasks?id=" + task.getId(), String.valueOf(task.getId()), task.getTitle())));
        }
        sb.append("</ul>\n");
        return createHtmlFooter(sb).toString();
    }


    private String buildSingleTaskPage(long id) {
        Task task;
        try {
            task = dba.getTaskByID(id);
        } catch (ElementNotFoundException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = createHtmlHeader(task.getTitle());
        sb.append("<a href=\"/tasks\">Retour</a>\n");
        sb.append("<form method=\"POST\">");
        sb.append(String.format("ID: %d <br>\n", task.getId()));
        if (task.getCreator() != null) {
            sb.append(String.format("Created By: %s<br>\n",
                    makeLink("/users?id=" + task.getCreator().getId(),
                            task.getCreator().getFirstName(), task.getCreator().getLastName())));
        }
        sb.append(createInputLine("Terminee&nbsp;", "done", String.valueOf(task.isDone()), "checkbox"));
        sb.append(createInputLine("Title", "title", task.getTitle()));
        sb.append(createInputLine("Description", "description", task.getDescription()));
        sb.append("<button class=\"btn btn-success\">OK</button>\n");
        sb.append("</form>\n");
        return createHtmlFooter(sb).toString();
    }


    public void handlePost(Request request, Socket client) throws IOException {
        String body = request.getBody();
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
                handleGet(request, client);
        }
    }
}
