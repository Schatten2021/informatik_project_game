package logging;

/**
 * The different levels that can be logged as.
 */
public enum Level {
    /**
     * All messages get logged.
     * Practically the same as {@link #DEBUG}, but if there were another logging-level below {@link #DEBUG}, it would also be logged.
     */
    ALL(0),
    /**
     * Sends a debug message.
     * Used for debugging, not recommended to be on in production.
     */
    DEBUG(10),
    /**
     * Sends an Info.
     * This can be used in a beta, but it's not recommended to be used in full prod.
     */
    INFO(20),
    /**
     * Sends a warning.
     * This may be used in production as these and above are the important messages that you always want to get.
     */
    WARNING(30),
    /**
     * For when an error happens.
     * Recommended for Production as a logging level.
     */
    ERROR(40),
    /**
     * When Something goes truly, horribly wrong.
     * Even when no other message gets logged, this still will.
     */
    FATAL(50);

    /**
     * The severity of the level.
     * Used to check if a message should be logged.
     */
    private final int severity;

    /**
     * Constructor for the enum Level.
     * @param severity How severe the given level is.
     *                 The higher, the more it gets logged.
     */
    Level(int severity) {
        this.severity = severity;
    }

    /**
     * Checks that the message is in scope for a logger.
     * @param level The level of the message.
     * @return Whether the message is within the scope of this level.
     */
    public boolean inScope(Level level) {
        return this.severity <= level.severity;
    }
}
