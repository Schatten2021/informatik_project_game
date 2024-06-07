package logging;

public class ConsoleLogger implements LoggingHandler {
    @Override
    public void log(String loggerName, String message, Level level) {
        System.out.printf("[[%s]] [%s] %s%n", loggerName, level.name(), message);
    }
}
