package Network.Packets.Fields;

import java.io.IOException;
import java.io.InputStream;

public class FloatField implements Field {
    public final float value;
    public FloatField(float value) {
        this.value = value;
    }

    public byte[] getBytes() {
        return new IntegerField(Float.floatToIntBits(value)).getBytes();
    }

    public static FloatField fromStream(InputStream stream) throws IOException {
        IntegerField data = IntegerField.fromStream(stream);
        return new FloatField(Float.intBitsToFloat(data.value));
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
