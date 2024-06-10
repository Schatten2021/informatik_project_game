package logging;

public interface LoggingHandler {
    void log(String loggerName, String message, Level level);
}
