package Network.Packets.Downstream;

import Network.Packets.Fields.AbilityField;
import Network.Packets.Fields.ArrayField;
import Network.Packets.Packet;

import java.io.IOException;
import java.io.InputStream;

public class Abilities extends Packet {
    public static final byte id = 0x04;
    public final ArrayField<AbilityField> abilities;
    public Abilities(ArrayField<AbilityField> abilities) {
        this.abilities = abilities;
    }

    @Override
    public byte[] toBytes() {
        byte[] abilitiesBytes = this.abilities.getBytes();
        byte[] bytes = new byte[abilitiesBytes.length + 1];
        bytes[0] = id;
        System.arraycopy(abilitiesBytes, 0, bytes, 1, abilitiesBytes.length);
        return bytes;
    }

    public static Abilities fromStream(InputStream stream) throws IOException {
        return new Abilities(ArrayField.fromStream(stream, AbilityField.class));
    }
}
