package Network.Packets.Upstream;
import Network.Packets.Fields.StringField;
import Network.Packets.Packet;
import logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Login extends Packet {
    public static final byte id = 0x01;
    public final String username;
    public final String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public byte[] toBytes() {
        byte[] usernameBytes = new StringField(username).getBytes();
        byte[] passwordBytes = new StringField(password).getBytes();
        byte[] bytes = new byte[usernameBytes.length + passwordBytes.length + 1];
        bytes[0] = id;
        System.arraycopy(usernameBytes, 0, bytes, 1, usernameBytes.length);
        System.arraycopy(passwordBytes, 0, bytes, usernameBytes.length + 1, passwordBytes.length);
        new Logger("Network.Packets.Upstream.Login").fdebug("Sending Packet Login with bytes %s", Arrays.toString(bytes));
        return bytes;
    }

    public static Login fromStream(InputStream stream) throws IOException {
        return new Login(StringField.fromStream(stream).value, StringField.fromStream(stream).value);
    }

    @Override
    public String toString() {
        return String.format("<Packet \"Login\" for \"%s\">", this.username);
    }
}
