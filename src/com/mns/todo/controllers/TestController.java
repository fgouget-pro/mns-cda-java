package com.mns.todo.controllers;

import com.fgouget.framework.server.RequestHandler;
import com.fgouget.framework.server.annotations.Controller;
import com.fgouget.framework.server.annotations.HANDLE;
import com.fgouget.framework.server.model.Response;
import com.mns.todo.database.DatabaseAccess;

import java.util.HashMap;

@Controller(route="/test")
public class TestController extends RequestHandler {


    private final DatabaseAccess dba;

    public TestController(DatabaseAccess dba) {
        this.dba = dba;
    }
    
    
    @HANDLE
    public String handleGet(Response r){
        var users = dba.getUsers();
        HashMap<String, Object> context = new HashMap<>();
        context.put("users", users);
        r.setContext(context);
        return "users";
    }

}
