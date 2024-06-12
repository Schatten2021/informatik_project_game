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
        byte[] bytes = new byte[4];
        int pos = 0;
        int available = stream.available();
        // ensure that the call never blocks because unexpectedly receiving too few bytes and the stream not being closed.
        while (available > 0 && pos < 4) {
            int readBytes = Math.min(available, 4);
            System.arraycopy(stream.readNBytes(readBytes), 0, bytes, pos, readBytes);
            pos += readBytes;
            available = stream.available();
        }
        if (pos < 4)
            throw new IOException("Stream didn't contain enough bytes");
        return new IntegerField(bytes);
    }
}
