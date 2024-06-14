package Network;
import logging.Level;
import logging.Logger;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Logger root = new Logger("root");
        root.setLevel(Level.ALL);
        root.addHandler(new logging.ConsoleLogger());
        Server server = new Server("localhost", 8080);
        server.login("test", "123");
    }
}
