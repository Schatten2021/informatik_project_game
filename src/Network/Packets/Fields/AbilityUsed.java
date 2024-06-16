package Network.Packets.Fields;

import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class AbilityUsed implements Field {
    public final IntegerField abilityId;
    public final FloatField value;

    public AbilityUsed(IntegerField abilityId, FloatField value) {
        this.abilityId = abilityId;
        this.value = value;
    }
    public AbilityUsed(int abilityId, float value) {
        this.abilityId = new IntegerField(abilityId);
        this.value = new FloatField(value);
    }

    @Override
    public byte[] getBytes() {
        return util.concat(abilityId.getBytes(), value.getBytes());
    }
    public static AbilityUsed fromStream(InputStream stream) throws IOException {
        return new AbilityUsed(IntegerField.fromStream(stream), FloatField.fromStream(stream));
    }
}
