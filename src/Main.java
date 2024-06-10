import logging.*;

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger("Logging.Test.Logger");
        logger.setLevel(Level.ALL);
        logger.addHandler(new ConsoleLogger());
        logger.debug("Hello World");
    }
}