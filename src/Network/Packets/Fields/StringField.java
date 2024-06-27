package Network.Packets.Fields;

import Network.Packets.util;
import logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class StringField implements Field {
    public final String value;
    private static final Logger logger = new Logger("Network.Packets.Fields.String");
    public StringField(String value){
        this.value = value;
    }

    @Override
    public byte[] getBytes() {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        IntegerField size = new IntegerField(stringBytes.length);
        byte[] bytes = util.concat(size.getBytes(), stringBytes);
        logger.fdebug("StringField with value %s got compressed to %s from string bytes %s", this.value, Arrays.toString(bytes), Arrays.toString(stringBytes));
        return bytes;
    }
    public static StringField fromStream(InputStream stream) throws IOException {
        IntegerField size = IntegerField.fromStream(stream);
        int pos = 0;
        int available = stream.available();
        byte[] bytes = new byte[size.value];
        long readStartTime = System.currentTimeMillis();
        while ((available > 0 || (System.currentTimeMillis() - readStartTime) < readTimeoutDuration) && pos < bytes.length) {
            int readCount = Math.min(available, bytes.length - pos);
            System.arraycopy(stream.readNBytes(readCount), 0, bytes, pos, readCount);
            available = stream.available();
            pos += readCount;
        }
        logger.debug(Arrays.toString(bytes));
        if (pos < bytes.length) {
            logger.debug(bytes.length);
            logger.debug(Arrays.toString(bytes));
            throw new IOException("Stream did not read enough bytes");
        }
        return new StringField(new String(bytes, StandardCharsets.UTF_8));
    }
    public String toString() {
        return '"' + this.value + "'";
    }
}
