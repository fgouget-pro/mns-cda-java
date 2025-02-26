package com.mns.todo.database;

import com.mns.todo.model.Task;
import com.mns.todo.model.User;
import me.xdrop.jrand.JRand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseSeeder {

    public void seed(){
        DatabaseAccess dba = DatabaseAccess.getInstance();
        List<User> users = generateUsers(100);
        users.forEach(dba::addUser);
        var savedUsers = dba.getUsers();
        List<Task> tasks = generateTasks(100);
        tasks.forEach(task -> {
            task.setCreator(savedUsers.get(new Random().nextInt(savedUsers.size())));
            dba.addTask(task);
        });

    }


    private List<User> generateUsers(int number){
        var firstNames = JRand.firstname();
        var lastNames = JRand.lastname();
        List<User> users = new ArrayList<>();
        for (int i=0; i<number; i++){
            var user = new User(firstNames.gen(), lastNames.gen());
            users.add(user);
        }
        return users;
    }


    private List<Task> generateTasks(int number){
        var wordGenerator = JRand.word();
        var paragraphGenerator = JRand.paragraph();
        List<Task> tasks = new ArrayList<>();
        for (int i=0; i<number; i++){
            Task t = new Task();
            t.setTitle(wordGenerator.gen());
            t.setDescription(paragraphGenerator.gen());
            tasks.add(t);
        }
        return tasks;
    }
}
