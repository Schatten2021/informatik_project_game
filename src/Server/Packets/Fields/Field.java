package Server.Packets.Fields;

import jdk.jshell.spi.ExecutionControl;

import java.io.IOException;
import java.io.InputStream;

public interface Field {
    byte[] getBytes();

    static Object fromStream(InputStream stream) throws IOException {
        throw new RuntimeException(new ExecutionControl.NotImplementedException("fromStream not implemented"));
    }
}
