package com.mns.todo.database;

import com.mns.todo.model.Task;
import com.mns.todo.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final List<User> users = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();
    private Connection connection;

    private DatabaseAccess(){
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:db1");
            createTables();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    private void createTables() throws SQLException {
        connection.createStatement().executeUpdate("CREATE TABLE USERS (id number, firstName varchar(255), lastName varchar(255));");
        connection.createStatement().executeUpdate("CREATE TABLE TASKS (name varchar(255), description varchar(MAX));");
        ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES;");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }





    public void addUser(User user) {
        users.add(user);
        try {
            connection.createStatement()
                    .executeUpdate("INSERT INTO USERS (id, firstName, lastName) VALUES (" + user.getId() + ", '" + user.getFirstName()+ "','"+ user.getLastName()+"')");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addTask(Task task) {

        tasks.add(task);
    }

    public List<User> getUsers() {
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM USERS");
            var users = new ArrayList<User>();
            while (rs.next()) {
                User user = new User(rs.getLong(1), rs.getString(2), rs.getString(3));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
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
