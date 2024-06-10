package Server.Packets.Upstream;

import Server.Packets.Packet;

import java.io.IOException;
import java.io.InputStream;

public class RoundFinished extends Packet {
    public static final byte id = 0x03;

    @Override
    public byte[] toBytes() {
        return new byte[]{id};
    }

    public static RoundFinished fromStream(InputStream stream) throws IOException {
        return new RoundFinished();
    }
}
