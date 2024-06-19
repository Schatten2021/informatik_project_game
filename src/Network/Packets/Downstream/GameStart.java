package Network.Packets.Downstream;

import Network.Packets.Fields.AbilityUsed;
import Network.Packets.Fields.ArrayField;
import Network.Packets.Fields.FloatField;
import Network.Packets.Fields.StringField;
import Network.Packets.Packet;
import Network.Packets.util;

import java.io.IOException;
import java.io.InputStream;

public class GameStart extends Packet {
    public static final byte id = 0x01;
    public final FloatField HP;
    public final FloatField HPRegen;
    public final FloatField MP;
    public final FloatField MPRegen;
    public final StringField otherName;
    public final FloatField otherHP;
    public final FloatField otherHPRegen;
    public final FloatField otherMP;
    public final FloatField otherMPRegen;
    public final ArrayField<AbilityUsed> abilitiesUsedSoFar;
    public GameStart(FloatField HP,
                     FloatField HPRegen,
                     FloatField MP,
                     FloatField MPRegen,
                     StringField otherName,
                     FloatField otherHP,
                     FloatField otherHPRegen,
                     FloatField otherMP,
                     FloatField otherMPRegen,
                     ArrayField<AbilityUsed> abilitiesUsedSoFar) {
        this.HP = HP;
        this.HPRegen = HPRegen;
        this.MP = MP;
        this.MPRegen = MPRegen;
        this.otherName = otherName;
        this.otherHP = otherHP;
        this.otherHPRegen = otherHPRegen;
        this.otherMP = otherMP;
        this.otherMPRegen = otherMPRegen;
        this.abilitiesUsedSoFar = abilitiesUsedSoFar;
    }
    public GameStart(float HP, float HPRegen, float MP, float MPRegen,  String otherName, float otherHP, float otherHPRegen, float otherMP, float otherMPRegen, AbilityUsed[] abilitiesUsedSoFar) {
        this.HP = new FloatField(HP);
        this.HPRegen = new FloatField(HPRegen);
        this.MP = new FloatField(MP);
        this.MPRegen = new FloatField(MPRegen);
        this.otherName = new StringField(otherName);
        this.otherHP = new FloatField(otherHP);
        this.otherHPRegen = new FloatField(otherHPRegen);
        this.otherMP = new FloatField(otherMP);
        this.otherMPRegen = new FloatField(otherMPRegen);
        this.abilitiesUsedSoFar = new ArrayField<>(abilitiesUsedSoFar);
    }

    @Override
    public byte[] toBytes() {
        return util.generateBytes(id, this.HP, this.HPRegen, this.MP, this.MPRegen, this.otherName, this.otherHP, this.otherHPRegen, this.otherMP, this.otherMPRegen);
    }

    public static GameStart fromStream(InputStream stream) throws IOException {
        return new GameStart(
                FloatField.fromStream(stream),
                FloatField.fromStream(stream),
                FloatField.fromStream(stream),
                FloatField.fromStream(stream),
                StringField.fromStream(stream),
                FloatField.fromStream(stream),
                FloatField.fromStream(stream),
                FloatField.fromStream(stream),
                FloatField.fromStream(stream),
                ArrayField.fromStream(stream, AbilityUsed.class)
        );
    }

    @Override
    public String toString() {
        String format = "<Packet \"GameStart\" (%.2f HP, +%.2f HP/round; %.2f MP, +%.2f MP/round) against \"%s\" (%.2f HP, +%.2f HP/round; %.2f MP, +%.2f MP/round)>";
        return String.format(format, this.HP.value, this.HPRegen.value, this.MP.value, this.MPRegen.value, this.otherName.value, this.otherHP.value, this.otherHPRegen.value, this.otherMP.value, this.otherMPRegen.value);
    }
}
