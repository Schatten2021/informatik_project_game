package Server.Packets.Fields;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StringField implements Field {
    public final String value;
    public StringField(String value){
        this.value = value;
    }

    @Override
    public byte[] getBytes() {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        IntegerField size = new IntegerField(stringBytes.length);
        byte[] arr = new byte[stringBytes.length + 4];
        System.arraycopy(size.getBytes(), 0, arr, 0, 4);
        System.arraycopy(stringBytes, 0, arr, 4, stringBytes.length);
        return arr;
    }
    public static StringField fromStream(InputStream stream) throws IOException {
        IntegerField size = IntegerField.fromStream(stream);
        byte[] bytes = stream.readNBytes(size.value);
        return new StringField(new String(bytes, StandardCharsets.UTF_8));
    }
}