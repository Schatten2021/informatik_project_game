package Network.Connection;

import Abitur.Queue;
import Network.Packets.Downstream.*;
import Network.Packets.Error;
import Network.Packets.Heartbeat;
import Network.Packets.Packet;
import Network.Packets.Upstream.Login;
import Network.Packets.Upstream.SignUP;
import logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

public class Connection {
    // data transport
    public final Queue<Packet> incoming = new Queue<>();
    public final Queue<Packet> outgoing = new Queue<>();
    public State status = State.NOT_CONNECTED;

    // connection information
    private final SocketAddress address;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    // account verification
    private String username = null;
    private String password = null;

    // auto update
    private Thread thread;

    // settings
    private static final long HeartbeatFrequency = 1000; //millis
    private static final int maxRetries = 3;

    // technical things
    private final Logger logger = new Logger("Network.Connection");
    private long lastHeartbeat = 0;
    private boolean isSignup = false;

    // constructors
    public Connection(String host, int port) {
        this(host, port, true);
    }
    public Connection(String host, int port, boolean autoUpdate) {
        this.address = new InetSocketAddress(host, port);
        if (autoUpdate)
            this.thread = new Thread(() -> {
                while (this.socket != null && this.socket.isConnected() && !this.socket.isClosed()) {
                    this.update();
                }
                this.logger.info("stopping thread");
            });
    }

    // account stuff
    public void login(String username, String password) {
        this.username = username;
        this.password = password;
        this.isSignup = false;
    }
    public void signup(String username, String password) {
        this.username = username;
        this.password = password;
        this.isSignup = true;
    }
    private synchronized void login() throws IOException {
        if (this.username == null || this.password == null) {
            this.logger.error("Username and password must be supplied");
            this.disconnect();
            return;
        }
        this.write(new Login(this.username, this.password));
        this.status = State.OK;
    }
    private synchronized void signup() throws IOException {
        if (this.username == null || this.password == null) {
            this.logger.error("Username and password must be supplied");
            this.disconnect();
            return;
        }
        this.write(new SignUP(this.username, this.password));
        this.status = State.OK;
        this.isSignup = false;
    }

    // connection stuff
    public void update() {
        if (this.socket == null) {
            this.logger.error("Socket is null");
            this.disconnect();
            return;
        }
        try {
            this.read();
            if (!this.outgoing.isEmpty()) {
                this.write(this.outgoing.front());
                this.outgoing.dequeue();
            }
            if (System.currentTimeMillis() - this.lastHeartbeat > HeartbeatFrequency) {
                this.write(new Heartbeat());
                this.lastHeartbeat = System.currentTimeMillis();
            }
        } catch (IOException e) {
            this.logger.ferror("Error trying to update connection (%s)", e);
            this.disconnect();
        }
    }
    private void connect() throws IOException {
        if (this.socket != null && (this.socket.isConnected() && !this.socket.isClosed())) {
            this.logger.warn("already connected");
            return;
        }
        this.logger.info("Connecting");
        this.socket = new Socket();
        this.socket.connect(address);
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
        this.status = State.NOT_LOGGED_IN;

        if (this.isSignup)
            this.signup();
        else
            this.login();
    }
    private void disconnect() {
        if (this.socket == null)
            return;
        this.logger.info("Disconnecting");
        try {
            this.socket.close();
        } catch (IOException e) {
            this.logger.ferror("Couldn't close socket because \"%s\"", e.getMessage());
        }
        this.status = State.DISCONNECTED;
        if (this.thread != null && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (InterruptedException e) {
                this.logger.ferror("Couldn't join thread because \"%s\"", e.getMessage());
            }
        }
    }
    private void reconnect() throws IOException {
        this.logger.warn("reconnecting");
        Exception[] exceptions = new Exception[maxRetries];
        for (int i = 0; i < maxRetries; i++) {
            try {
                this.disconnect();
                this.connect();
                this.logger.info("successfully reconnected");
                return;
            } catch (IOException e) {
                exceptions[i] = e;
            }
        }
        this.logger.ferror("Couldn't reconnect due to %s", Arrays.toString(exceptions));
        throw new IOException("Couldn't reconnect");
    }

    // read
    private void read() throws IOException {
        try {
            this.attemptRead();
        } catch (IOException e) {
            this.logger.fwarn("failed to read from socket because of \"%s\"", e);
            this.reconnect();
            this.attemptRead();
        }
    }
    private void attemptRead() throws IOException{
        if (this.in.available() <= 0)
            return;
        byte id = this.in.readNBytes(1)[0];
        Packet packet;
        switch (id) {
            case Heartbeat.id:
                packet = Heartbeat.fromStream(this.in);
                break;
            case GameStart.id:
                packet = GameStart.fromStream(this.in);
                break;
            case GameEnd.id:
                packet = GameEnd.fromStream(this.in);
                break;
            case RoundEnd.id:
                packet = RoundEnd.fromStream(this.in);
                break;
            case Abilities.id:
                packet = Abilities.fromStream(this.in);
                break;
            case Effects.id:
                packet = Effects.fromStream(this.in);
                break;
            case Error.id:
                packet = Error.fromStream(this.in);
                break;
            default:
                throw new IOException("Invalid packet id");
        }
        if (!(packet instanceof Heartbeat)) {
            this.logger.fdebug("got packet %s", packet);
        }
        this.incoming.enqueue(packet);
    }

    // write
    private void write(Packet packet) throws IOException {
        try {
            this.attemptWrite(packet);
        } catch (IOException e) {
            this.logger.fwarn("failed to write to socket because of \"%s\"", e);
            this.reconnect();
            this.attemptWrite(packet);
        }
        if (!(packet instanceof Heartbeat))
            this.logger.fdebug("Sent packet %s", packet);
    }
    private void attemptWrite(Packet packet) throws IOException {
        this.out.write(packet.toBytes());
    }

    // management
    public void start() throws IOException {
        this.connect();
        if (this.thread != null)
            this.thread.start();
    }
    public void stop() {
        this.disconnect();
        this.status = State.NOT_CONNECTED;
    }
    public State getStatus() {
        return this.status;
    }
    public String getName() {
        return this.username;
    }
}
