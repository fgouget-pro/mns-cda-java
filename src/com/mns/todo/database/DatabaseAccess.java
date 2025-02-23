package com.mns.todo.database;

import com.mns.todo.model.Task;
import com.mns.todo.model.User;
import com.mns.todo.model.mapper.TaskMapper;
import com.mns.todo.model.mapper.UserMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    public static final String CREATE_TABLE_USERS = "CREATE TABLE USERS (id bigint auto_increment primary key, firstName varchar(255), lastName varchar(255));";
    public static final String CREATE_TABLE_TASKS = "CREATE TABLE TASKS (id bigint auto_increment primary key, title varchar(255), description varchar(MAX), done boolean, creator_id bigint);";
    public static final String CREATE_USER = "INSERT INTO USERS (firstName, lastName) VALUES (?, ?)";
    public static final String CREATE_TASK = "INSERT INTO TASKS (title, description, done, creator_id) VALUES (?, ?, ?, ?)";
    public static final String GET_USER = "SELECT * FROM USERS WHERE id = ?";
    public static final String GET_TASK = "SELECT * FROM TASKS WHERE id = ?";
    public static final String UPDATE_USER = "UPDATE USERS SET firstName = ?, lastName = ? WHERE id = ?";
    public static final String UPDATE_TASK = "UPDATE TASKS SET title = ?, description = ?, done= ? WHERE id = ?";
    public static final String LIST_USERS = "SELECT * FROM USERS";
    public static final String LIST_TASKS = "SELECT * FROM TASKS";
    public static final String LIST_TASKS_DONE = "SELECT * FROM TASKS WHERE done = ?";


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
        connection.createStatement().executeUpdate(CREATE_TABLE_USERS);
        connection.createStatement().executeUpdate(CREATE_TABLE_TASKS);
        ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES;");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    public void addUser(User user) {
        try {
            var statement = connection
                    .prepareStatement(CREATE_USER);
            //statement.setLong(1, user.getId());
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addTask(Task task) {
        try {
            var statement = connection
                    .prepareStatement(CREATE_TASK);
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setBoolean(3, task.isDone());
            statement.setLong(4, task.getCreator().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<User> getUsers() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(LIST_USERS);
            return new UserMapper().toUserList(rs);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<Task> getTasks(boolean done){
        try {
            PreparedStatement statement = connection.prepareStatement(LIST_TASKS_DONE);
            statement.setBoolean(1, done);
            ResultSet rs = statement.executeQuery();
            return new TaskMapper().toTaskList(rs);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<Task> getTasks() {
        try {
            ResultSet rs = connection.createStatement().executeQuery(LIST_TASKS);
            return new TaskMapper().toTaskList(rs);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public Task getTaskByID(long id) throws ElementNotFoundException {
        try {
            var statement = connection.prepareStatement(GET_TASK);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            System.out.println(rs);
            if (rs.next()) {
                Task t = new TaskMapper().toTask(rs);
                long userid = rs.getLong(5);
                User u = getUserById(userid);
                t.setCreator(u);
                return t;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        throw new ElementNotFoundException("The User with ID <" + id + "> could not be found");
    }

    public User getUserById(long id) throws ElementNotFoundException {
        try {
            var statement = connection.prepareStatement(GET_USER);
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

    public void saveTask(Task task) {
        if (task.getId() == 0){
            addTask(task);
            return;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE_TASK);
            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setBoolean(3, task.isDone());
            statement.setLong(4, task.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public void saveUser(User u){
        if (u.getId() == 0){
            addUser(u);
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(UPDATE_USER);
            statement.setString(1, u.getFirstName());
            statement.setString(2, u.getLastName());
            statement.setLong(3, u.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


}
