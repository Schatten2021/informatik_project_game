package Server;

import java.io.IOException;

public class ConnectionError extends Exception {
    private final Exception originalException;
    public ConnectionError(IOException e) {
        this.originalException = e;
    }
    public ConnectionError(String message) {
        this.originalException = new Exception(message);
    }
    public ConnectionError() {
        this.originalException = new Exception();
    }
}
