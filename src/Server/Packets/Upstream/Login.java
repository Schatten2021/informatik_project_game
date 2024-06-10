package Server.Packets.Upstream;
import Server.Packets.Fields.StringField;
import Server.Packets.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends Packet {
    public static final byte id = 0x01;
    public String username;
    public String password;
    private static final MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Login(String username, String password) {
        this.username = username;
        this.password = new String(md.digest(password.getBytes()));
    }

    @Override
    public byte[] toBytes() {
        byte[] usernameBytes = username.getBytes();
        byte[] passwordBytes = password.getBytes();
        byte[] bytes = new byte[usernameBytes.length + passwordBytes.length + 1];
        bytes[0] = this.id;
        System.arraycopy(usernameBytes, 0, bytes, 1, usernameBytes.length);
        System.arraycopy(passwordBytes, 0, bytes, usernameBytes.length + 1, passwordBytes.length);
        return bytes;
    }

    public static Login fromStream(InputStream stream) throws IOException {
        return new Login(StringField.fromStream(stream).value, StringField.fromStream(stream).value);
    }
}
