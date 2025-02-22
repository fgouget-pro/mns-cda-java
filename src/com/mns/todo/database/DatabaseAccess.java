package com.mns.todo.database;

import com.mns.todo.model.Task;
import com.mns.todo.model.User;
import com.mns.todo.model.mapper.UserMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    /**  Sert à implementer le design pattern Singleton.*/
    private static DatabaseAccess instance;

    public static DatabaseAccess getInstance() {
        if (instance == null) {
            instance = new DatabaseAccess();
        }
        return instance;
    }

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
        connection.createStatement().executeUpdate("CREATE TABLE USERS (id bigint auto_increment primary key, firstName varchar(255), lastName varchar(255));");
        connection.createStatement().executeUpdate("CREATE TABLE TASKS (id bigint auto_increment primary key, name varchar(255), description varchar(MAX));");
        ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES;");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    public void addUser(User user) {
        try {
            var statement = connection
                    .prepareStatement("INSERT INTO USERS (firstName, lastName) VALUES (?, ?)");
            //statement.setLong(1, user.getId());
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.executeUpdate();
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
            return new UserMapper().toUserList(rs);
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
        try {
            var statement = connection.prepareStatement("SELECT * FROM USERS WHERE id = ?");
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new UserMapper().toUser(rs);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new ElementNotFoundException("The User with ID <" + id + "> could not be found");
    }

    public void saveUser(User u){
        if (u.getId() == 0){
            addUser(u);
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("UPDATE USERS SET firstName = ?, lastName = ? WHERE id = ?");
            statement.setString(1, u.getFirstName());
            statement.setString(2, u.getLastName());
            statement.setLong(3, u.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


}
