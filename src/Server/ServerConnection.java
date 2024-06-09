package Server;

import java.io.*;
import java.net.*;


public class ServerConnection {
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public ServerConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }
}
