package Network.dataStructures;

public class AbilityUsed {
    public final Ability ability;
    public final int round;
    public final boolean player1;
    public final float value;

    public AbilityUsed(Ability ability, int round, boolean player1, float value) {
        this.ability = ability;
        this.round = round;
        this.player1 = player1;
        this.value = value;
    }
    public AbilityUsed(int abilityId, int round, boolean player1, float value) {
        this.ability = Ability.load(abilityId);
        this.round = round;
        this.player1 = player1;
        this.value = value;
    }
    public AbilityUsed(Network.Packets.Fields.AbilityUsed packet) {
        this.ability = Ability.load(packet.abilityId.value);
        this.round = packet.round.value;
        this.player1 = false; // assume that this constructor is called with an incoming packet.
        this.value = packet.value.value;
    }
}
