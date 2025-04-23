import com.fgouget.framework.context.ApplicationContext;
import org.h2.tools.Server;

import java.sql.SQLException;


static int PORT = 8080;

public static void main(String[] args) throws SQLException {
    Server server = Server.createWebServer().start(); // Console web H2
    var appContext = new ApplicationContext();
    appContext.scanPackage("com.mns.todo");
    appContext.startServer(PORT);
}