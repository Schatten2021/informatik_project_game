package Network;

import Abitur.Queue;
import Network.Connection.Connection;
import Network.Connection.State;
import Network.Packets.Packet;
import logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Connects to a server.
 * Used to transport data such as player data to the server where it will be stored and processed.
 * Required for online play.
 */
public class Server {
    /**
     * The internal connection.
     */
    private final Connection connection;
    /**
     * The logger for the Server module.
     */
    private final Logger logger = new Logger("Server");

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    /**
     * The {@link MessageDigest}, that can hash byte arrays with <a href="https://wikipedia.org/wiki/SHA-2">SHA-256</a>.
     */
    private static final MessageDigest md;
    // required for to get the SHA-256 hash.
    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructor for {@link Server}.
     * @param host The ip or url of the server to connect to.
     * @param port The port of the server. (e.g. 8080)
     * @throws IOException When an IOException occurs, most likely because it couldn't connect to the server.
     */
    public Server(String host, int port) throws IOException {
        this.connection = new Connection(host, port);
    }

    /**
     * Logs the current user in and start the connection.
     * @param username The username of the user.
     * @param password The password to the account of the user.
     */
    public void login(String username, String password) throws IOException {
        String hash = hash(password);
        logger.debug("logging in with hash: " + hash);
        this.connection.login(username, hash);
        this.connection.start();
    }

    /**
     * Similar to {@link #login}, but creates a new account.
     * @param username The name of the player.
     * @param password The password to the account.
     * @throws IOException When an IOException occurs while trying to sign up.
     */
    public void signup(String username, String password) throws IOException {
        String hash = hash(password);
        logger.debug("signing up with hash: " + hash);
        this.connection.signup(username, hash);
        this.connection.start();
    };

    /**
     * Helper function for {@link #login} and {@link #signup}, because the passwords are hashed (not transmitting and storing plaintext passwords, duh).
     * @param string The password to be hashed.
     * @return The hexadecimal representation of the bytes.
     */
    private static String hash(String string) {
        byte[] digest = md.digest(string.getBytes());
        byte[] b64Encoded = Base64.getEncoder().encode(digest);
        return new String(b64Encoded);
    }

    public Queue<Packet> getPackets() {
        return this.connection.incoming;
    }
    public void send(Packet packet) {
        this.connection.outgoing.enqueue(packet);
    }

    public boolean alive() {
        return this.connection.status != State.DISCONNECTED && this.connection.status != State.NOT_CONNECTED;
    }
}
