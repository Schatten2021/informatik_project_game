package Server;

import java.io.IOException;

public class Server {
    ServerConnection conn;
    public Server(String host, int port) throws ConnectionError {
        try {
            this.conn = new ServerConnection(host, port);
        } catch (IOException e) {
            throw new ConnectionError(e);
        }
    }
}
