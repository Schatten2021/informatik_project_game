package logging;
import Abitur.*;

import java.util.HashMap;

public class Logger {
    private static class logger {
        public final String name;
        private final HashMap<String, logger> subLoggers = new HashMap<>();
        private final List<LoggingHandler> handlers;
        private Level level;

        public logger(String name, List<LoggingHandler> handlers, Level level) {
            this.name = name;
            this.handlers = handlers;
            this.level = level;
        }
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
        public void addHandler(LoggingHandler handler) {
            this.handlers.append(handler);
        }
        public void setLevel(Level level) {
            this.level = level;
            for (logger subLogger : subLoggers.values()) {
                subLogger.setLevel(level);
            }
        }
        public Level getLevel() {
            return this.level;
        }
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


    private static final logger root = new logger("root", new List<>(), Level.INFO);
    private final logger _logger;

    public Logger(String name) {
        if (name.equals("root")) {
            this._logger = root;
        } else {
            this._logger = root.getSubLogger(name);
        }
    }

    public void setLevel(Level level) {
        this._logger.setLevel(level);
    }
    public void addHandler(LoggingHandler handler) {
        this._logger.addHandler(handler);
    }
    public Level getLevel(Level level) {
        return this._logger.getLevel();
    }

    public void log(Object message, Level level) {
        this._logger.log(message.toString(), level);
    }
    public void info(Object message) {
        this._logger.log(message.toString(), Level.INFO);
    }
    public void debug(Object message) {
        this._logger.log(message.toString(), Level.DEBUG);
    }
    public void warn(Object message) {
        this._logger.log(message.toString(), Level.WARNING);
    }
    public void error(Object message) {
        this._logger.log(message.toString(), Level.ERROR);
    }
    public void fatal(Object message) {
        this._logger.log(message.toString(), Level.FATAL);
    }
}
