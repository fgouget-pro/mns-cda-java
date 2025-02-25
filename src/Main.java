import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.model.Task;
import com.mns.todo.model.User;
import com.mns.todo.server.Request;
import org.h2.tools.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, IOException {
        DatabaseAccess dba = DatabaseAccess.getInstance();
        Server s = Server.createWebServer().start();
        User u = new User("Florent", "Gouget");
        dba.addUser(u);
        List<User> users = dba.getUsers();
        Task task = new Task("title",
                "description",
                u);
        dba.addTask(task);
        Task myTask = dba.getTaskById(1);
     //   System.out.println(myTask);



        try (ServerSocket server = new ServerSocket(8080)){
            while (true) {
                Socket client = server.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                Request request = new Request();

                String firstLine = reader.readLine();
                String[] splitFirstLine = firstLine.split(" ");

                request.setMethod(splitFirstLine[0]);
                request.setPath(splitFirstLine[1]);
                request.setProtocol(splitFirstLine[2]);


                String line;
                while (!(line = reader.readLine()).isBlank()) {
                    var h = line.split(": ");
                    request.getHeaders().put(h[0], h[1]);
                }

                System.out.println(request);

                OutputStream outputStream = client.getOutputStream();
                outputStream.write("HTTP/1.1 200\r\n".getBytes());
                outputStream.write("Content-Type: text/html\r\n".getBytes());
                outputStream.write("\r\n".getBytes());
                outputStream.write("<html><body><h1>Hello World</h1></body></html>".getBytes());
                outputStream.write("\r\n\r\n".getBytes());
                outputStream.flush();
                outputStream.close();
                reader.close();
                client.close();
            }
        }


    }
}