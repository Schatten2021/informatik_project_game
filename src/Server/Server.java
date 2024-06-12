package Server;

import Server.Connection.Connection;

import java.io.IOException;

public class Server {
    private final Connection connection;
    public Server(String host, int port) throws IOException {
        this.connection = new Connection(host, port);
    }

    /**
     * Logs the current user in.
     * @param username The username of the user.
     * @param password The password to the account of the user.
     */
    public void login(String username, String password) throws IOException {
        this.connection.login(username, password);
        System.out.println("Starting connection");
        this.connection.start();
    }
}
