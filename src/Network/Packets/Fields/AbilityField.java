package Network.Packets.Fields;

import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class AbilityField implements Field{
    public final IntegerField id;
    public final StringField name;
    public final FloatField cost;
    public final ArrayField<IntegerField> effects;
    public AbilityField(IntegerField id, StringField name, FloatField cost, ArrayField<IntegerField> effects) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.effects = effects;
    }

    @Override
    public byte[] getBytes() {
        return util.concat(id.getBytes(), name.getBytes(), cost.getBytes(), effects.getBytes());
    }

    public static AbilityField fromStream(InputStream stream) throws IOException {
        return new AbilityField(IntegerField.fromStream(stream), // id
                StringField.fromStream(stream), //name
                FloatField.fromStream(stream), // cost
                ArrayField.fromStream(stream, IntegerField.class) // effects
        );
    }

    public String toString() {
        return String.format("<Ability %s (%s)>", this.name.value, this.effects.toString());
    }
}
