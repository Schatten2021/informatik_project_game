package Server.Packets.Downstream;

import Server.Packets.Fields.StringField;
import Server.Packets.Packet;

import java.io.IOException;
import java.io.InputStream;

public class GameStart extends Packet {
    public static final byte id = 0x01;
    public final String otherName;
    public GameStart(String otherName) {
        this.otherName = otherName;
    }

    @Override
    public byte[] toBytes() {
        byte[] stringBytes = otherName.getBytes();
        byte[] result = new byte[stringBytes.length + 1];
        result[0] = id;
        System.arraycopy(stringBytes, 0, result, 1, stringBytes.length);
        return result;
    }

    public static GameStart fromStream(InputStream stream) throws IOException {
        return new GameStart(StringField.fromStream(stream).value);
    }
}
