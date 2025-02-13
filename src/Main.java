import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.ElementNotFoundException;
import com.mns.todo.database.ModelDatabaseAccess;
import com.mns.todo.model.DatedTask;
import com.mns.todo.model.Task;
import com.mns.todo.model.User;
import com.mns.todo.model.build.TaskBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    static DatabaseAccess dba = DatabaseAccess.getInstance();

    public static void main(String[] args) {


        Task builtTask = new TaskBuilder()
                .setCreator(new User())
                .setDescription("This is a test")
                // .setTitle("This is a title")
                .setDone(false)
                .createTask();






        var scanner = new Scanner(System.in);
        System.out.println("What is your name?");
        String name = "Florent";//scanner.nextLine();
        User u = new User(name);
        dba.addUser(u);

        Task task = new Task();
        task.setCreator(u);
        task.setDone(false);
        task.setTitle("My com.mns.todo.model.Task");
        task.setDescription("My Description");
        dba.addTask(task);

        Task task2 = new Task();
        task2.setCreator(u);
        task2.setDone(false);
        task2.setTitle("My Task2");
        task2.setDescription("My Description2");
        dba.addTask(task2);

        DatedTask task3 = new DatedTask();
        task3.setCreator(u);
        task3.setDone(false);
        task3.setTitle("My Task3");
        task3.setDescription("My Description3");
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            task3.setDueDate(formatter.parse("01/01/2026"));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        dba.addTask(task3);


        List<Task> tasks = dba.getTasks();
        for (Task t : tasks) {
            System.out.println(t);
        }

        System.out.println("What ID to get?");
        long id = scanner.nextLong();
        try {
            Task found = dba.getTaskByID(id);
            System.out.println(found);
        } catch (ElementNotFoundException e) {
            System.out.println(e.getMessage());
        }


        ModelDatabaseAccess<User> dbUser = new ModelDatabaseAccess<>();
        ModelDatabaseAccess<Task> dbTask = new ModelDatabaseAccess<>();

        try {
            User myUser = dbUser.getElementByID(10);
            Task myTask = dbTask.getElementByID(10);
        } catch (ElementNotFoundException e){

        }

        scanner.close();



    }
}