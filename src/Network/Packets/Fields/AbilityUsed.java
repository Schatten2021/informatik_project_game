package Network.Packets.Fields;

import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class AbilityUsed implements Field {
    public final IntegerField abilityId;
    public final FloatField value;
    public final IntegerField round;
    public final BooleanField usedByOther;

    public AbilityUsed(IntegerField abilityId, FloatField value, IntegerField round, BooleanField used) {
        this.abilityId = abilityId;
        this.value = value;
        this.round = round;
        this.usedByOther = used;
    }

    public AbilityUsed(int id, float value, int round, boolean used) {
        this.abilityId = new IntegerField(id);
        this.value = new FloatField(value);
        this.round = new IntegerField(round);
        this.usedByOther = new BooleanField(used);
    }

    @Override
    public byte[] getBytes() {
        return util.concat(abilityId.getBytes(), value.getBytes(), round.getBytes(), usedByOther.getBytes());
    }
    public static AbilityUsed fromStream(InputStream stream) throws IOException {
        return new AbilityUsed(
                IntegerField.fromStream(stream), // id
                FloatField.fromStream(stream), // value
                IntegerField.fromStream(stream), // round
                BooleanField.fromStream(stream) // used
        );
    }
}
