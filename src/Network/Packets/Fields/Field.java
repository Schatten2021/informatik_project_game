package Network.Packets.Fields;

import java.io.IOException;
import java.io.InputStream;

public interface Field {
    byte[] getBytes();
    long readTimeoutDuration = 100;

    static Object fromStream(InputStream stream) throws IOException {
        return null;
    }
}
