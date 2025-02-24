import com.mns.todo.controllers.TaskController;
import com.mns.todo.controllers.UserController;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.DatabaseSeeder;
import com.mns.todo.server.Router;
import org.h2.tools.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    /**
     * DBA Centralise et abstrait tous les accès à la base de donnée
     */
    static DatabaseAccess dba = DatabaseAccess.getInstance();
    static int PORT = 8080;

    private static final Router router = new Router();

    public static void main(String[] args) throws IOException, SQLException {
        /* DatabaseSeeder se charge de créer tous les éléments en base de donnée au démarrage */
        DatabaseSeeder dbSeeder = new DatabaseSeeder();
        dbSeeder.seed();
        Server server = Server.createWebServer().start(); // Console web H2
        System.out.println("DB successfully seeded! Starting server...");

        router.addHandler("/users", new UserController());
        router.addHandler("/tasks", new TaskController());
        try (ExecutorService service = Executors.newFixedThreadPool(10)) {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server started on port " + PORT);
                while (true) {
                    System.out.println("Waiting for connection...");
                    try {
                        Socket client = serverSocket.accept();
                        service.submit(() -> router.getClientHandler(client).handleClient());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            server.shutdown();
        }
    }


}