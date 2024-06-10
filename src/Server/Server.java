package Server;

import Server.Connection.Connection;

public class Server {
    private final Connection connection;
    public Server(String host, int port) {
        this.connection = new Connection(host, port);
        this.connection.start();
    }

    /**
     * Logs the current user in.
     * @param username The username of the user.
     * @param password The password to the account of the user.
     */
    public void login(String username, String password) {
        this.connection.login(username, password);
    }
}
