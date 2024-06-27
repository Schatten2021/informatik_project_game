package Network.Packets.Fields;

import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class EffectField implements Field {
    public final int id;
    public final String name;
    public final int valueAffected;
    public final int time;
    public final float min;
    public final float max;
    public final boolean isPercent;
    public final boolean hitsSelf;

    public EffectField(int id, String name, int valueAffected, int time, float min, float max, boolean isPercent, boolean hitsSelf) {
        this.id = id;
        this.name = name;
        this.valueAffected = valueAffected;
        this.time = time;
        this.min = min;
        this.max = max;
        this.isPercent = isPercent;
        this.hitsSelf = hitsSelf;
    }

    public byte[] getBytes() {
        return util.concat(id.getBytes(), name.getBytes(), valueAffected.getBytes(), time.getBytes(), min.getBytes(), max.getBytes(), isPercent.getBytes(), hitsSelf.getBytes());
    }

    public static EffectField fromStream(InputStream stream) throws IOException {
        IntegerField id = IntegerField.fromStream(stream);
        StringField name = StringField.fromStream(stream);
        IntegerField valueAffected = IntegerField.fromStream(stream);
        IntegerField time = IntegerField.fromStream(stream);
        FloatField min = FloatField.fromStream(stream);
        FloatField max = FloatField.fromStream(stream);
        BooleanField isPercent = BooleanField.fromStream(stream);
        BooleanField hitsSelf = BooleanField.fromStream(stream);
        return new EffectField(id.value, name.value, valueAffected.value, time.value, min.value, max.value, isPercent.value, hitsSelf.value);
    }
    public static EffectField fromStream() {
        throw new RuntimeException("Empty fromStream called");
    }
    public static EffectField fromStream() {
        throw new RuntimeException("Empty fromStream called");
    }
    public static EffectField fromStream() {
        throw new RuntimeException("Empty fromStream called");
    }
    public String toString() {
        return String.format("<Effect %s (value affected: %d) %.2f - %.2f (%d turns)>", this.name, this.valueAffected, this.min, this.max, this.time);
    }
}
