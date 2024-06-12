package Server;

import Server.Connection.Connection;
import logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Server {
    private final Connection connection;
    private final Logger logger = new Logger("Server");

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private static final MessageDigest md;
    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public Server(String host, int port) throws IOException {
        this.connection = new Connection(host, port);
    }

    /**
     * Logs the current user in.
     * @param username The username of the user.
     * @param password The password to the account of the user.
     */
    public void login(String username, String password) throws IOException {
        String hash = bytesToHex(md.digest(password.getBytes()));
        logger.debug("logging in with hash: " + hash);
        this.connection.login(username, hash);
        this.connection.start();
    }
    public void signup(String username, String password) throws IOException {
        String hash = bytesToHex(md.digest(password.getBytes()));
        logger.debug("signing up with hash: " + hash);
        this.connection.signup(username, hash);
        this.connection.start();
    };
    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
