package Network.dataStructures;

import Abitur.List;
import Network.Packets.Downstream.GameStart;

import java.util.ArrayList;

public class Game {
    private final Player player1; //always the user
    private final Player player2;
    public List<AbilityUsed> abilitiesUsed;
    public int round;
    public Game(GameStart packet, String playerName) {
        this.player1 = new Player(playerName, packet.HP.value, packet.MP.value, packet.HPRegen.value, packet.MPRegen.value);
        this.player2 = new Player(packet.otherName.value, packet.otherHP.value, packet.otherMP.value, packet.otherMPRegen.value, packet.otherMPRegen.value);
        this.round = packet.round.value;
    }
    public void add(AbilityUsed abilityUsed) {
        this.abilitiesUsed.append(abilityUsed);
    }
    public Effect[] getActiveEffects(boolean player1) {
        List<Effect> effects = new List<>();
        this.abilitiesUsed.toFirst();
        while (this.abilitiesUsed.hasAccess()) {
            AbilityUsed ability = this.abilitiesUsed.getContent();
            this.abilitiesUsed.next();
            Effect[] abilitiesEffects = ability.ability.effects;
            for (int i = 0; i < abilitiesEffects.length; i++) {
                Effect effect = abilitiesEffects[i];
                boolean hits = effect.hitSelf == ability.player1 == player1;
                boolean isActive = (effect.duration + ability.round) >= this.round;
                if (hits && isActive) {
                    effects.append(effect);
                }
            }
        }
        return new Effect[0];
    }
}
