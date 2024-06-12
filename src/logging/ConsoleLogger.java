package logging;

/**
 * A simple logger that logs to the console.
 */
public class ConsoleLogger implements LoggingHandler {

    /**
     * Logs the given message with the given Level to the console.
     * Warnings, Errors and Fatals get logged to {@link System#err System.err}, while other logs go to {@link System#out System.out}.
     * @param loggerName The name of the logger that logs the message.
     * @param message The actual message to be logged.
     * @param level The level that this message was logged at.
     */
    @Override
    public void log(String loggerName, String message, Level level) {
        if (level == Level.ERROR || level == Level.FATAL || level == Level.WARNING)
            System.err.printf("[[%s]] [%s] %s%n", loggerName, level.name(), message);
        else
            System.out.printf("[[%s]] [%s] %s%n", loggerName, level.name(), message);
    }
}
