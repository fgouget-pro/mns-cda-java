import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.DatabaseSeeder;
import me.xdrop.jrand.JRand;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Main {
    static DatabaseAccess dba = DatabaseAccess.getInstance();

    public static void main(String[] args) {
        DatabaseSeeder dbSeeder = new DatabaseSeeder();
        dbSeeder.seed();

        DatabaseAccess dba = DatabaseAccess.getInstance();
        var users = dba.getUsers();

        users.forEach(System.out::println);

    }
}