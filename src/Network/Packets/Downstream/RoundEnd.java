package Network.Packets.Downstream;

import Network.Packets.Fields.ArrayField;
import Network.Packets.Fields.IntegerField;
import Network.Packets.Packet;
import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class RoundEnd extends Packet {
    public static final byte id = 0x03;
    public final ArrayField<IntegerField> abilitiesUsed;
    public RoundEnd(ArrayField<IntegerField> abilitiesUsed) {
        this.abilitiesUsed = abilitiesUsed;
    }
    public RoundEnd(int[] abilityIds) {
        IntegerField[] fields = new IntegerField[abilityIds.length];
        for (int i = 0; i < abilityIds.length; i++) {
            fields[i] = new IntegerField(abilityIds[i]);
        }
        this.abilitiesUsed = new ArrayField<>(fields);
    }

    @Override
    public byte[] toBytes() {
        return util.generateBytes(id, abilitiesUsed);
    }

    public static RoundEnd fromStream(InputStream stream) throws IOException {
        return new RoundEnd(ArrayField.fromStream(stream, IntegerField.class));
    }
}
