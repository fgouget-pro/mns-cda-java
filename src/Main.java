import com.mns.todo.controllers.TaskController;
import com.mns.todo.controllers.UserController;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.DatabaseSeeder;
import com.mns.todo.model.User;
import com.mns.todo.server.*;
import org.h2.tools.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;


public class Main {

    /** DBA Centralise et abstrait tous les accès à la base de donnée */
    static DatabaseAccess dba = DatabaseAccess.getInstance();
    static int PORT = 8080;

    private static final Router router = new Router();

    public static void main(String[] args) throws IOException, SQLException {
        /* DatabaseSeeder se charge de créer tous les éléments en base de donnée au démarrage */
        DatabaseSeeder dbSeeder = new DatabaseSeeder();
        dbSeeder.seed();
        Server server = Server.createWebServer().start();
        System.out.println("DB successfully seeded! Starting server...");


        router.addHandler("/users", new UserController());
        router.addHandler("/tasks", new TaskController());

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                System.out.println("Waiting for connection...");
                try (Socket client = serverSocket.accept()) {
                    router.getClientHandler(client).handleClient();
                }
            }
        } finally {
            server.stop();
        }
    }




}