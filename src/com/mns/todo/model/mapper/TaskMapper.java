package com.mns.todo.model.mapper;

import com.mns.todo.model.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskMapper {

    public Task toTask(ResultSet rs){
        try {
            if (rs.getMetaData().getColumnCount() == 5) {
                return new Task(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    public List<Task> toTaskList(ResultSet rs) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        while (rs.next()) {
            try {
                tasks.add(toTask(rs));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return tasks;
    }


}
