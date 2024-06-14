package Network.Packets.Downstream;

import Network.Packets.Fields.ArrayField;
import Network.Packets.Fields.IntegerField;
import Network.Packets.Packet;

import java.io.DataInputStream;
import java.io.IOException;

public class PlayerInfo extends Packet {
    public static final byte id = 0x06;
    public final ArrayField<IntegerField> availableAbilities;

    public PlayerInfo(ArrayField<IntegerField> availableAbilities) {
        this.availableAbilities = availableAbilities;
    }

    public byte[] toBytes() {
        byte[] abilityBytes = this.availableAbilities.getBytes();
        byte[] bytes = new byte[abilityBytes.length + 1];
        bytes[0] = id;
        System.arraycopy(abilityBytes, 0, bytes, 1, abilityBytes.length);
        return bytes;
    }

    public static PlayerInfo fromStream(DataInputStream in) throws IOException {
        return new PlayerInfo(ArrayField.fromStream(in));
    }
}
