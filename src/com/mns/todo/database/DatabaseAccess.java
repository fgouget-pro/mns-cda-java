package com.mns.todo.database;

import com.mns.todo.model.Task;
import com.mns.todo.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS USERS (id bigint auto_increment primary key, firstName varchar(255), lastName varchar(255));";
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS TASKS (id bigint auto_increment primary key, title varchar(255), description varchar(MAX), done boolean, creator_id bigint);";
    private static final String GET_USERS = "SELECT * FROM USERS";
    private static final String CREATE_USER = "INSERT INTO USERS (firstName, lastName) VALUES (?, ?);";
    private static final String CREATE_TASK = "INSERT INTO TASKS (title, description, done, creator_id) VALUES (?, ?, ?, ?);";
    private static final String GET_TASKS = "SELECT * FROM TASKS";
    private static final String GET_TASK_BY_ID = "SELECT * FROM TASKS WHERE id = ?;";
    private static final String GET_USER_BY_ID = "SELECT * FROM USERS WHERE id = ?;";

    private static DatabaseAccess instance;

    public static DatabaseAccess getInstance() {
        if (instance == null) {
            instance = new DatabaseAccess();
        }
        return instance;
    }

    private Connection connection;

    private DatabaseAccess() {
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
    }


    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(GET_USERS);
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                User user = new User(id, firstName, lastName);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return users;
    }



    public void addUser(User user) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_USER);
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addTask(Task task) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TASK);
            preparedStatement.setString(1, task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setBoolean(3, task.isDone());
            if (task.getCreator() != null) {
                preparedStatement.setLong(4, task.getCreator().getId());
            } else {
                preparedStatement.setNull(4, Types.BIGINT);
            }
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                task.setId(rs.getLong(1));
            }
        }catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery(GET_TASKS);
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getLong(1));
                task.setTitle(rs.getString(2));
                task.setDescription(rs.getString(3));
                task.setDone(rs.getBoolean(4));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tasks;
    }


    public Task getTaskById(long id) {
        try {
            PreparedStatement statement = connection.prepareStatement(GET_TASK_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
               Task t = new Task();
               t.setId(resultSet.getLong(1));
               t.setTitle(resultSet.getString(2));
               t.setDescription(resultSet.getString(3));
               t.setDone(resultSet.getBoolean(4));
               long userId = resultSet.getLong(5);
               User user = getUserById(userId);
               if (user != null) {
                   t.setCreator(user);
               }
                return t;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    public User getUserById(long id) {
        try {
            PreparedStatement statement = connection.prepareStatement(GET_USER_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong(1));
                user.setFirstName(resultSet.getString(2));
                user.setLastName(resultSet.getString(3));
                return user;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    private static final String SELECT_USERS = "SELECT * FROM USERS";
    private static final String INSERT_USER = "INSERT INTO USERS (id, firstName, lastName) VALUES (?,?,?)";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM USERS WHERE ID=?";
    private final List<Task> tasks = new ArrayList<>();


}
