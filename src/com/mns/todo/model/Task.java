package com.mns.todo.model;

import java.util.Objects;

public class Task implements Model{

    static {
        System.out.println("com.mns.todo.model.Task static initialised");
    }


    public Task(long id, String title, String description, boolean done) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.done = done;
        this.creator = creator;
    }

    public Task(){
        this.id = Math.round(Math.random()*1000);
    }

    public Task(String title, String description){
        this();
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, User user){
        this(title, description);
        this.creator = user;
    }


    protected long id;
    protected String description;
    protected String title;
    protected boolean done;
    protected User creator;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "com.mns.todo.model.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", done=" + done +
                ", creator=" + creator +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
