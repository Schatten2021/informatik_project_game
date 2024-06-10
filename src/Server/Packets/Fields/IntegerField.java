package Server.Packets.Fields;

import java.io.IOException;
import java.io.InputStream;

public class IntegerField implements Field {
    public final int value;
    public IntegerField(int value) {
        this.value = value;
    }
    public IntegerField(byte[] value) {
        if (value.length != 4)
            throw new IllegalArgumentException("byte array length must be 4 to be an integer");
        this.value = (value[0] & 0xFF)
                | ((value[1] & 0xFF) << 8)
                | ((value[2] & 0xFF) << 16)
                | ((value[3] & 0xFF) << 24);
    }
    public byte[] getBytes() {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static IntegerField fromStream(InputStream stream) throws IOException {
        byte[] bytes = stream.readNBytes(4);
        return new IntegerField(bytes);
    }
}
