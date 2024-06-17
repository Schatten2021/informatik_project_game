package Network.Packets.Downstream;

import Network.Packets.Fields.AbilityUsed;
import Network.Packets.Fields.ArrayField;
import Network.Packets.Packet;
import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class RoundEnd extends Packet {
    public static final byte id = 0x03;
    public final ArrayField<AbilityUsed> abilitiesUsed;
    public RoundEnd(ArrayField<AbilityUsed> abilitiesUsed) {
        this.abilitiesUsed = abilitiesUsed;
    }

    @Override
    public byte[] toBytes() {
        return util.generateBytes(id, abilitiesUsed);
    }

    public static RoundEnd fromStream(InputStream stream) throws IOException {
        return new RoundEnd(ArrayField.fromStream(stream, AbilityUsed.class));
    }
}
