package Network.Packets.Downstream;

import Network.Packets.Fields.ArrayField;
import Network.Packets.Fields.EffectField;
import Network.Packets.Packet;
import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class Effects extends Packet {
    public static final byte id = 0x05;
    public final ArrayField<EffectField> effects;

    public Effects(ArrayField<EffectField> effects) {
        this.effects = effects;
    }

    @Override
    public byte[] toBytes() {
        return util.generateBytes(id, this.effects);
    }

    public static Effects fromStream(InputStream stream) throws IOException {
        return new Effects(ArrayField.fromStream(stream, EffectField.class));
    }
    public String toString() {
        return String.format("<Effects %s>", effects.toString());
    }
}
