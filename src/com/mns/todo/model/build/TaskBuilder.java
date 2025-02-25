package com.mns.todo.model.build;

import com.mns.todo.model.Task;
import com.mns.todo.model.User;

public class TaskBuilder {
    protected String description;
    protected String title;
    protected boolean done;
    protected User creator;

    public TaskBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    public TaskBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    public TaskBuilder setDone(boolean done) {
        this.done = done;
        return this;
    }
    public TaskBuilder setCreator(User creator) {
        this.creator = creator;
        return this;
    }

    protected void populateTask(Task task) {
        task.setDescription(description);
        task.setTitle(title);
        task.setDone(done);
        task.setCreator(creator);
    }

    public Task createTask() {
        Task task = new Task();
        populateTask(task);
        return task;
    }


}
