package logging;
import Abitur.*;

import java.util.HashMap;

/**
 * Class for logging different things.
 * <br>
 * Logging can be done on multiple different levels, including DEBUG, INFO and ERROR.
 * Loggers can also inherit from each other, copying the level and handlers.
 * <br>
 * A logger that inherits from another logger is divided by a dot from its parent's name (e.g. the logger "root.example" is a child of "root").
 * <br>
 * Loggers will implicitly inherit from the logger "root", which can be accessed via {@code new Logger("root")}.
 * This also means that something like {@code new Logger("root.example")} will not give the logger "root.example" but "root.root.example".
 * To get "root.example" you have to do {@code new Logger("example")}!
 * <br>
 * Loggers can have multiple handlers and a level.
 * Child loggers always have all the handlers of the parent logger, but parent loggers don't automatically get the handlers from the child logger.
 * <br>
 * The level of a logger determines what gets logged and what not.
 * If you have a logger configured to have the level {@link Level#ERROR} it will not log warnings, infos or debug messages.
 */
public class Logger {
    /**
     * This is the class that does most of the actual work.
     */
    private static class logger {
        /**
         * The full name of the logger.
         * The logger "root.example" will have the name "root.example".
         */
        public final String name;
        /**
         * The child loggers associated by their partial name.
         * The logger "root.example" will be under the name "example" in the HashMap for the logger root.
         * THe hashMap only contains direct descendants, so "root.parent.child" will not be found in the logger "root", but in "roo.parent".
         */
        private final HashMap<String, logger> subLoggers = new HashMap<>();
        /**
         * All the handlers this logger currently has.
         */
        private final List<LoggingHandler> handlers;
        /**
         * The minimum Level a message must have to be logged.
         */
        private Level level;

        /**
         * Constructor of the {@link logger} class.
         * @param name The full name of the logger.
         *             "root.example" will get "root.example" here.
         * @param handlers A list of all the handlers.
         *                 Given by the parent logger so that the child has all the handlers the parent has.
         * @param level The level of this logger.
         *              Also given by the parent for the same reason.
         */
        public logger(String name, List<LoggingHandler> handlers, Level level) {
            this.name = name;
            this.handlers = handlers;
            this.level = level;
        }

        /**
         * Returns the sub-logger with the given name.
         * If no sub-logger with the given name exists, one is created.
         * @param name The partial name of the sub-logger.
         *             For "root.parent.child" this would be "parent.child" for the logger "root".
         * @return The sub-logger with the given name.
         */
        public logger getSubLogger(String name) {
            if (name.isEmpty()) {
                return this;
            }
            String[] names = name.split("\\.", 2);
            if (!subLoggers.containsKey(names[0])) {
                String loggerFullName = this.name + "." + names[0];
                subLoggers.put(names[0], new logger(loggerFullName, handlers, this.level));
            }

            // get the sub logger
            if (names.length < 2) {
                return this.subLoggers.get(name);
            }
            return this.subLoggers.get(names[0]).getSubLogger(names[1]);
        }

        /**
         * Adds a handler to this logger and all of its children.
         * @param handler The handler to add.
         */
        public void addHandler(LoggingHandler handler) {
            this.handlers.append(handler);
            for (logger subLogger : subLoggers.values()) {
                subLogger.addHandler(handler);
            }
        }

        /**
         * Recursively sets the level of this logger and all its children.
         * @param level The new level of this logger.
         *              Will only log messages of this level and above.
         */
        public void setLevel(Level level) {
            this.level = level;
            for (logger subLogger : subLoggers.values()) {
                subLogger.setLevel(level);
            }
        }

        /**
         * Logs a message using all of its handlers (excluding child loggers).
         * @param message The message to be logged
         * @param level The level of the message to be logged.
         *              If it is smaller than the level of this logger, it will just be ignored.
         */
        public void log(String message, Level level) {
            if (!this.level.inScope(level)) {
                return;
            }
            this.handlers.toFirst();
            while (this.handlers.hasAccess()) {
                this.handlers.getContent().log(this.name, message, level);
                this.handlers.next();
            }
        }
    }

    /**
     * The root logger.
     * Makes it a lot easier in a lot of places, because you can "globally" set a level by configuring the root logger.
     */
    private static final logger root = new logger("root", new List<>(), Level.INFO);
    /**
     * The {@link logger} instance that does all the work.
     * Required since the {@link Logger} class is basically just a wrapper.
     */
    private final logger _logger;

    /**
     * Constructor for {@link Logger}.
     * @param name The name of the logger.
     *             If it is a child, separate the name of the parent with a dot, so the child "example" of the logger "test" would be "example.test".
     *             However, all loggers inherit from the logger "root", which can be accessed via "root".
     *             Inheriting from root does not require "root.x", but simply "x".
     */
    public Logger(String name) {
        if (name.equals("root")) {
            this._logger = root;
        } else {
            this._logger = root.getSubLogger(name);
        }
    }

    /**
     * Sets the level of this logger and all its children.
     * <br>
     * Warning! This will overwrite the level of the children.
     * @param level The new level of the logger.
     */
    public void setLevel(Level level) {
        this._logger.setLevel(level);
    }

    /**
     * Adds a handler to this logger and all of its children.
     * @param handler The new handler to add.
     */
    public void addHandler(LoggingHandler handler) {
        this._logger.addHandler(handler);
    }

    /**
     * logs a message using the handlers provided by {@link #addHandler}
     * @param message The message to log. Will be turned into a string using the {@link #toString()} method.
     * @param level The level of the message to log.
     *              If this is lower than the level of this logger, the message will be ignored.
     */
    public void log(Object message, Level level) {
        this._logger.log(message.toString(), level);
    }

    /**
     * Shorthand for {@code {@link #log}(message, {@link Level#INFO}}.
     * Will only log if the logger is configured as {@link Level#INFO} or lower.
     * @param message The message to log. Will be turned into a string using the {@link #toString()} method.
     */
    public void info(Object message) {
        this.log(message, Level.INFO);
    }

    /**
     * Shorthand for {@code {@link #log}(message, {@link Level#DEBUG}}.
     * Will only log if the logger is configured as {@link Level#DEBUG} or {@link Level#ALL}.
     * @param message The message to log. Will be turned into a string using the {@link #toString()} method.
     */
    public void debug(Object message) {
        this.log(message, Level.DEBUG);
    }

    /**
     * Shorthand for {@code {@link #log}(message, {@link Level#WARNING}}.
     * Will only log if the logger is configured as {@link Level#WARNING} or lower.
     * @param message The message to log. Will be turned into a string using the {@link #toString()} method.
     */
    public void warn(Object message) {
        this.log(message, Level.WARNING);
    }

    /**
     * Shorthand for {@code {@link #log}(message, {@link Level#ERROR}}.
     * Will only log if the logger is configured as {@link Level#ERROR} or lower.
     * @param message The message to log. Will be turned into a string using the {@link #toString()} method.
     */
    public void error(Object message) {
        this.log(message, Level.ERROR);
    }

    /**
     * Shorthand for {@code {@link #log}(message, {@link Level#FATAL}}.
     * Will only log if the logger is configured as {@link Level#FATAL} or lower.
     * @param message The message to log. Will be turned into a string using the {@link #toString()} method.
     */
    public void fatal(Object message) {
        this.log(message, Level.FATAL);
    }
}
