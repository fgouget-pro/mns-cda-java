package com.mns.todo.model.mapper;

import com.mns.todo.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public User toUser(ResultSet rs) {
        try {
            if (rs.getMetaData().getColumnCount() == 3) {
                return new User(rs.getLong(1), rs.getString(2), rs.getString(3));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<User> toUserList(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            try {
                users.add(toUser(rs));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return users;
    }



}
