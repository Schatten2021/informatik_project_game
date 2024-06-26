package Network.Packets.Fields;

import logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IntegerField implements Field {
    public final int value;
    private static final Logger logger = new Logger("Network.Packets.Fields.IntegerField");
    public IntegerField(int value) {
        this.value = value;
    }
    public IntegerField(byte[] value) {
        if (value.length != 4)
            throw new IllegalArgumentException("byte array length must be 4 to be an integer");
        this.value = (value[3] & 0xFF)
                | ((value[2] & 0xFF) << 8)
                | ((value[1] & 0xFF) << 16)
                | ((value[0] & 0xFF) << 24);
    }
    public byte[] getBytes() {
        byte[] result = new byte[4];
        result[0] = (byte) ((this.value & 0xFF000000) >> 24);
        result[1] = (byte) ((this.value & 0x00FF0000) >> 16);
        result[2] = (byte) ((this.value & 0x0000FF00) >> 8);
        result[3] = (byte) (this.value & 0x000000FF);
        return result;
    }

    public static IntegerField fromStream(InputStream stream) throws IOException {
        byte[] bytes = new byte[4];
        int pos = 0;
        int available = stream.available();
        // ensure that the call never blocks because unexpectedly receiving too few bytes and the stream not being closed.
        long readStartTime = System.currentTimeMillis();
        while ((available > 0 || (System.currentTimeMillis() - readStartTime) > readTimeoutDuration) && pos < bytes.length) {
            int readBytes = Math.min(available, 4 - pos);
            System.arraycopy(stream.readNBytes(readBytes), 0, bytes, pos, readBytes);
            pos += readBytes;
            available = stream.available();
        }
        logger.fdebug("read bytes %s", Arrays.toString(bytes));
        if (pos < 4)
            throw new IOException("Stream didn't contain enough bytes");
        return new IntegerField(bytes);
    }
    public String toString() {
        return String.valueOf(this.value);
    }
}
