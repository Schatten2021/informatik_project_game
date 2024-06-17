package Network.Packets.Fields;

import java.io.IOException;
import java.io.InputStream;

public class AbilityField implements Field{
    public final IntegerField id;
    public final StringField name;
    public final ArrayField<IntegerField> effects;
    public AbilityField(IntegerField id, StringField name, ArrayField<IntegerField> effects) {
        this.id = id;
        this.name = name;
        this.effects = effects;
    }

    @Override
    public byte[] getBytes() {
        byte[] idBytes = id.getBytes();
        byte[] nameBytes = name.getBytes();
        byte[] effectBytes = effects.getBytes();
        int size = idBytes.length + nameBytes.length + effectBytes.length;
        byte[] data = new byte[size];
        System.arraycopy(idBytes, 0, data, 0, idBytes.length);
        System.arraycopy(nameBytes, 0, data, idBytes.length, nameBytes.length);
        System.arraycopy(effectBytes, 0, data, idBytes.length + nameBytes.length, effectBytes.length);
        return data;
    }

    public static AbilityField fromStream(InputStream stream) throws IOException {
        IntegerField id = IntegerField.fromStream(stream);
        StringField name = StringField.fromStream(stream);
        ArrayField<IntegerField> EffectIds = ArrayField.fromStream(stream, IntegerField.class);
        return new AbilityField(id, name, EffectIds);
    }

    public String toString() {
        return String.format("<Ability %s (%s)>", this.name.value, this.effects.toString());
    }
}
