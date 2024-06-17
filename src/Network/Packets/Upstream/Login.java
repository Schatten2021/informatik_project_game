package Network.Packets.Upstream;
import Network.Packets.Fields.StringField;
import Network.Packets.Packet;

import java.io.IOException;
import java.io.InputStream;

public class Login extends Packet {
    public static final byte id = 0x01;
    public String username;
    public String password;

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
