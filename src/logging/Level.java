package logging;

public enum Level {
    ALL(0),
    DEBUG(10),
    INFO(20),
    WARNING(30),
    ERROR(40),
    FATAL(50);

    private final int severity;

    Level(int severity) {
        this.severity = severity;
    }
    public boolean inScope(Level level) {
        return this.severity <= level.severity;
    }
}
