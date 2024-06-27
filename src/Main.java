import Network.Test;
import logging.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Logger logger = new Logger("Logging.Test.Logger");
        logger.setLevel(Level.ALL);
        logger.addHandler(new ConsoleLogger());
        logger.debug("Hello World");
        new Test("test", "123", false);
        new Test("test2", "123", true);
    }
}