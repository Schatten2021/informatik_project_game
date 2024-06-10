package Server.Connection;

import Server.Packets.Downstream.*;
import Server.Packets.Heartbeat;
import Server.Packets.Error;
import Server.Packets.Packet;
import Abitur.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

import Server.Packets.Upstream.Login;
import logging.Logger;

public class Connection {
    public final Queue<Packet> incoming = new Queue<>();
    public final Queue<Packet> outgoing = new Queue<>();
    private State status = State.NOT_CONNECTED;

    private final SocketAddress address;
    private final Socket socket;
    private InputStream in;
    private OutputStream out;

    private String username = null;
    private String password = null;
    private boolean loggedIn = false;

    private Thread receiverThread;
    private Thread senderThread;

    private final Logger logger = new Logger("Server.Connection");
    private long lastHeartbeat = 0;
    private static final long HeartbeatFrequency = 10; //millis

    public Connection(String host, int port) {
        this.socket = new Socket();
        this.address = new InetSocketAddress(host, port);
    }
    public void login(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void start() {
        if (receiverThread == null) {
            receiverThread = new Thread(this::receiveThread);
            receiverThread.start();
        }
        if (senderThread == null) {
            this.senderThread = new Thread(this::senderThread);
            this.senderThread.start();
        }
    }
    public void stop() {
        try {
            this.disconnect();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        try {
            this.receiverThread.join();
        } catch (InterruptedException e) {
            this.logger.error(e.getMessage());
        }
        try {
            this.senderThread.join();
        } catch (InterruptedException e) {
            this.logger.error(e.getMessage());
        }
        this.status = State.NOT_CONNECTED;
    }

    // internal methods
    private void receiveThread() {
        while (this.senderThread.isAlive() && !this.socket.isClosed() && this.socket.isConnected()) {
            try {
                this.receivePacket();
            } catch (IOException e) {
                this.logger.error(e.getMessage());
                this.disconnect();
            }
        }
    }
    private void senderThread() {
        while (this.receiverThread.isAlive() && !this.socket.isClosed() && this.socket.isConnected()) {
            long now = System.currentTimeMillis();
            if (now - lastHeartbeat > HeartbeatFrequency) {
                this.lastHeartbeat = now;
                try {
                    this.sendPacket(new Heartbeat());
                } catch (IOException e) {
                    this.logger.error(e.getMessage());
                    this.disconnect();
                }
            }
            if (this.outgoing.isEmpty()) {
                continue;
            }
            try {
                this.sendPacket(this.outgoing.front());
            } catch (IOException e) {
                this.logger.error(e.getMessage());
                this.disconnect();
            }
            this.outgoing.dequeue();
        }
    }
    private synchronized void connect() throws IOException {
        this.loggedIn = false;
        this.socket.connect(this.address);
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.status = State.NOT_LOGGED_IN;
        this.login();
    }
    private synchronized void disconnect() {
        try {
            this.socket.close();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
        this.loggedIn = false;
        try {
            this.senderThread.join();
        } catch (InterruptedException e) {
            this.logger.error(e.getMessage());
        }
        try {
            this.receiverThread.join();
        } catch (InterruptedException e) {
            this.logger.error(e.getMessage());
        }
        this.senderThread = null;
        this.receiverThread = null;
    }
    private synchronized void reconnect() throws IOException {
        IOException exception = null;
        this.status = State.DISCONNECTED;
        for (int i = 0; i < 3; i++) {
            this.disconnect();
            try {
                this.connect();
                return;
            } catch (IOException e) {
                exception = e;
            }
        }
        throw exception;
    }
    private synchronized void login() throws IOException {
        if (this.username == null || this.password == null) {
            this.disconnect();
            this.logger.error("Username and Password not supplied");
        }
        this.sendPacket(new Login(this.username, this.password));
        this.loggedIn = true;
        this.status = State.OK;
    }

    private void sendPacket(Packet packet) throws IOException {
        try {
            this.trySendPacket(packet);
        } catch (IOException e) {
            this.reconnect();
            this.trySendPacket(packet);
        }
    }
    private void receivePacket() throws IOException {
        try {
            this.tryReceivePacket();
        } catch (IOException e) {
            this.reconnect();
            this.tryReceivePacket();
        }
    }


    private void trySendPacket(Packet packet) throws IOException {
        this.out.write(packet.toBytes());
    }
    private void tryReceivePacket() throws IOException {
        byte id = this.in.readNBytes(1)[0];
        Packet packet = switch (id) {
            case 0x00 -> Heartbeat.fromStream(this.in);
            case 0x01 -> GameStart.fromStream(this.in);
            case 0x02 -> GameEnd.fromStream(this.in);
            case 0x03 -> RoundEnd.fromStream(this.in);
            case 0x04 -> Abilities.fromStream(this.in);
            case 0x05 -> Effects.fromStream(this.in);
            case (byte) 0xFF -> Error.fromStream(this.in);
            default -> throw new IOException("Invalid packet id");
        };
        this.incoming.enqueue(packet);
    }
}