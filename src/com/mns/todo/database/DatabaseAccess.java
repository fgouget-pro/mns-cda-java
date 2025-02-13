package com.mns.todo.database;

import com.mns.todo.model.Task;
import com.mns.todo.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private static DatabaseAccess instance;

    public static DatabaseAccess getInstance() {
        if (instance == null) {
            instance = new DatabaseAccess();
        }
        return instance;
    }

    private List<User> users = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

    private DatabaseAccess(){}

    public void addUser(User user) {
        users.add(user);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTaskByID(long id) throws ElementNotFoundException {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        throw new ElementNotFoundException("The Task with ID <" + id + "> Could not be found");
    }

    public User getUserById(long id) throws ElementNotFoundException {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        throw new ElementNotFoundException("The user with ID <" + id + "> Could not be found");
    }


}
