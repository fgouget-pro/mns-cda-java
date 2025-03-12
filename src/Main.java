import com.fgouget.framework.context.ApplicationContext;
import com.mns.todo.database.DatabaseAccess;
import com.mns.todo.database.DatabaseSeeder;
import org.h2.tools.Server;

import java.io.IOException;
import java.sql.SQLException;


public class Main {
    /**
     * DBA Centralise et abstrait tous les accès à la base de donnée
     */
    static DatabaseAccess dba = DatabaseAccess.getInstance();
    static int PORT = 8080;


    public static void main(String[] args) throws IOException, SQLException {
        /* DatabaseSeeder se charge de créer tous les éléments en base de donnée au démarrage */
        DatabaseSeeder dbSeeder = new DatabaseSeeder();
        dbSeeder.seed();
        Server server = Server.createWebServer().start(); // Console web H2
        System.out.println("DB successfully seeded! Starting server...");
        var appContext = new ApplicationContext();
        appContext.scanPackage("com.mns.todo.controllers");
        appContext.startServer(8080);
    }


}