import com.mns.todo.database.DatabaseSeeder;
import com.mns.todo.server.ClientHandler;
import com.mns.todo.server.DemoEnum;
import org.h2.tools.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, IOException {
        new DatabaseSeeder().seed();
        Server s = Server.createWebServer().start();
        
        try (ServerSocket server = new ServerSocket(8080)) {
            while (true) {
                Socket client = server.accept();
                new ClientHandler().handle(client);
            }
        }
    }
}