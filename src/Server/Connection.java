package Server;

import Abitur.Queue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import logging.*;

public class Connection {
    private final Socket socket;
    private final SocketAddress address;
    private Thread thread;
    private OutputStream out;
    private InputStream in;
    private Queue<Packet> outgoingPackets;
    private Queue<Byte> incomingPackets;
    private static final Logger logger = new Logger("Server.Connection");
    private ConnectionStatus status = ConnectionStatus.IDLE;


    public Connection(String host, int port) {
        this.socket = new Socket();
        this.address = new InetSocketAddress(host, port);
        this.outgoingPackets = new Queue<>();
        this.incomingPackets = new Queue<>();
    }

    /**
     * Attempt to connect the socket to the given Address.
     * @throws IOException When an IO Exception occurs while trying to connect.
     */
    private void connect() throws IOException {
        this.socket.connect(this.address);
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
    }

    /**
     * Disconnects the socket.
     * @throws IOException When an IO Exception occurs while trying to connect.
     */
    private void disconnect() throws IOException {
        this.socket.close();
    }

    /**
     * Start the connection. Use this instead of {@link #connect()} because this will also start the auto reconnect.
     */
    public void start() {
        if (this.thread != null) {
            return;
        }
        this.thread = new Thread(this::threadMethod);
        this.thread.start();
        this.status = ConnectionStatus.OK;
    }
    public void stop() throws InterruptedException {
        if (this.thread == null) {
            return;
        }
        this.thread.interrupt();
        this.thread.join();
        this.thread = null;
        this.outgoingPackets = new Queue<>();
        this.incomingPackets = new Queue<>();
    }
    private void threadMethod() {
        try {
            while (!this.thread.isInterrupted()) {
                while (!this.outgoingPackets.isEmpty()) {
                    this.write(this.outgoingPackets.front());

                }
            }
            while (this.incomingDataAvailable()) {
                try {
                    read();
                } catch (IOException e) {
                    this.reconnect();
                    read();
                }
            }
            this.read();
        } catch (IOException e) {
            logger.error(e);
            this.thread.interrupt();
        }
    }
    private void reconnect() throws IOException {
        boolean connectionReset = false;
        IOException exception = null;
        // try to reconnect 3 times
        for (int i = 0; i < 3; i++) {
            try {
                // close the socket for good measure.
                this.socket.close();
            } catch (IOException _) {}
            try {
                // try to connect
                this.connect();
                // if the connection failed, this won't be reached, and we try again
                // else we have successfully reestablished the connection.
                connectionReset = true;
                break;
            } catch (IOException e) {
                exception = e;
            }
        }
        // if after 3 tries the connection still couldn't be established, we have a problem
        if (!connectionReset) {
            this.status = ConnectionStatus.DISCONNECTED;
            throw exception;
        }
    }
    private void write(Packet packet) throws IOException {
        try {
            // try to write the packet
            this.out.write(packet.toBytes());
        } catch (IOException e) {
            // we probably disconnected. Try to reconnect.
            if (e.getCause() instanceof SocketTimeoutException) {
                // If we can't reconnect, this will throw an exception
                this.reconnect();
            }
            // and try to write again.
            this.out.write(packet.toBytes());
        }
    }

    /**
     * Reads an integer worth of data
     * @throws IOException If an exception occurred (probably during read)
     */
    private void read() throws IOException {
        int data = this.in.read();
        this.incomingPackets.enqueue((byte) (data >> 24));
        this.incomingPackets.enqueue((byte) (data >> 16));
        this.incomingPackets.enqueue((byte) (data >> 8));
        this.incomingPackets.enqueue((byte) (data));
    }

    private boolean incomingDataAvailable() throws IOException {
        try {
            return this.in.available() > 0;
        } catch (IOException e) {
            this.reconnect();
            return this.in.available() > 0;
        }
    }
}
