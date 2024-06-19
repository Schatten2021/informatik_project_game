package Network.Packets.Fields;

import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class AbilityUsed implements Field {
    public final IntegerField abilityId;
    public final FloatField value;
    public final IntegerField round;

    public AbilityUsed(IntegerField abilityId, FloatField value, IntegerField round) {
        this.abilityId = abilityId;
        this.value = value;
        this.round = round;
    }

    public AbilityUsed(int id, float value, int round) {
        this.abilityId = new IntegerField(id);
        this.value = new FloatField(value);
        this.round = new IntegerField(round);
    }

    @Override
    public byte[] getBytes() {
        return util.concat(abilityId.getBytes(), value.getBytes(), round.getBytes());
    }
    public static AbilityUsed fromStream(InputStream stream) throws IOException {
        return new AbilityUsed(IntegerField.fromStream(stream), FloatField.fromStream(stream), IntegerField.fromStream(stream));
    }
}
