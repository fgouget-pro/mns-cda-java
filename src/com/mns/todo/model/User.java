package com.mns.todo.model;

import java.util.Objects;

public class User implements Model {

    private Long id;
    private String firstName;
    private String lastName;

    {
        this.id = Math.round(Math.random()*1000);
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public User(){
    }

    public User(String firstName) {
        this();
        this.firstName = firstName;
    }

    public User(String firstName, String lastName) {
        this(firstName);
        this.lastName = lastName;
    }

    public User(long id, String firstName, String lastName) {
        this(firstName, lastName);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
