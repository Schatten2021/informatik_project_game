package logging;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple logger that logs to the console.
 */
public class ConsoleLogger implements LoggingHandler {
    /**
     * Format in which the dates will be printed
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            System.err.printf("[[%s; %s]] [%s] %s%n", loggerName, dateFormat.format(new Date()), level.name(), message);
        else
            System.out.printf("[[%s; %s]] [%s] %s%n", loggerName, dateFormat.format(new Date()), level.name(), message);
    }
}
