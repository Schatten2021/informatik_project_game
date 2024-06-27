package Network.Packets.Fields;

import Network.Packets.util;


import java.io.IOException;
import java.io.InputStream;

public class EffectField implements Field {
    public final IntegerField id;
    public final StringField name;
    public final IntegerField valueAffected;
    public final IntegerField time;
    public final FloatField min;
    public final FloatField max;
    public final BooleanField isPercent;
    public final BooleanField hitsSelf;

    public EffectField(int id, String name, int valueAffected, int time, float min, float max, boolean isPercent, boolean hitsSelf) {
        this.id = new IntegerField(id);
        this.name = new StringField(name);
        this.valueAffected = new IntegerField(valueAffected);
        this.time = new IntegerField(time);
        this.min = new FloatField(min);
        this.max = new FloatField(max);
        this.isPercent = new BooleanField(isPercent);
        this.hitsSelf = new BooleanField(hitsSelf);
    }
    public EffectField(IntegerField id, StringField name, IntegerField valueAffected, IntegerField time, FloatField min, FloatField max, BooleanField isPercent, BooleanField hitsSelf) {
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
        return new EffectField(
                IntegerField.fromStream(stream), //id
                StringField.fromStream(stream), //name
                IntegerField.fromStream(stream), // time
                IntegerField.fromStream(stream), // valueAffected
                FloatField.fromStream(stream), // min
                FloatField.fromStream(stream), // max
                BooleanField.fromStream(stream), // isPercent
                BooleanField.fromStream(stream) // hitsSelf
        );
    }
    public static EffectField fromStream() {
        throw new RuntimeException("Empty fromStream called");
    }
    public String toString() {
        return String.format("<Effect %s (value affected: %d) %.2f - %.2f (%d turns)>", this.name.value, this.valueAffected.value, this.min.value, this.max.value, this.time.value);
    }
}
