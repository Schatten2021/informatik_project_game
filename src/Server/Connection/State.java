package Server.Connection;

public enum State {
    /**
     * The socket was disconnected, probably due to a network problem.
     */
    DISCONNECTED,
    /**
     * An error occurred in the connection between the socket and the server
     */
    ERROR,
    /**
     * The socket isn't yet connected or was disconnected in a controlled manner.
     */
    NOT_CONNECTED,
    /**
     * The socket didn't log in yet, has to be done first.
     */
    NOT_LOGGED_IN,
    /**
     * The connection is OK, nothing to worry about.
     */
    OK,
}
