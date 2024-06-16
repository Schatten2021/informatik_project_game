package Network.Packets.Fields;

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
        byte[] stringBytes = new StringField(this.name).getBytes();
        // ID: 4; Value affected: 4; Time: 4; Min: 4; max: 4; relative: 1; hitsSelf: 1; string: ?
        // = 4 * 5 + 1 + 1 + ? = 22+
        int size = 22 + stringBytes.length;
        byte[] finalBytes = new byte[size];
        System.arraycopy(new IntegerField(this.id).getBytes(), 0, finalBytes, 0, 4);
        System.arraycopy(stringBytes, 0, finalBytes, 4, stringBytes.length);
        System.arraycopy(new IntegerField(this.valueAffected).getBytes(), 0, finalBytes, 4 + stringBytes.length, 4);
        System.arraycopy(new IntegerField(this.time).getBytes(), 0, finalBytes, 8 + stringBytes.length, 4);
        System.arraycopy(new FloatField(this.min).getBytes(), 0, finalBytes, 12 + stringBytes.length, 4);
        System.arraycopy(new FloatField(this.max).getBytes(), 0, finalBytes, 16 + stringBytes.length, 4);
        System.arraycopy(new BooleanField(this.isPercent).getBytes(), 0, finalBytes, 20 + stringBytes.length, 1);
        System.arraycopy(new BooleanField(this.hitsSelf).getBytes(), 0, finalBytes, 21 + stringBytes.length, 1);
        return finalBytes;
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
    public String toString() {
        return String.format("<Effect %s (value affected: %d) %.2f - %.2f (%d turns)>", this.name, this.valueAffected, this.min, this.max, this.time);
    }
}
