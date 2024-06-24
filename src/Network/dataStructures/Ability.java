package Network.dataStructures;

import Network.Packets.Fields.Field;
import Network.Packets.Fields.IntegerField;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.*;

public class Ability {
    private static final HashMap<Integer, Ability> abilities = new HashMap<>();

    public final int id;
    public final String name;
    public final Effect[] effects;
    public final float cost;

    private Ability(int id, String name, Effect[] effects, float cost) {
        this.id = id;
        this.name = name;
        this.effects = effects;
        this.cost = cost;
    }
    public static Ability load(int id) {
        if (!abilities.containsKey(id)) {
            throw new NoSuchElementException(String.format("Effect with id %d does not exist", id));
        }
        return abilities.get(id);
    }
    public static Ability create(int id, String name, Effect[] effects, float cost) {
        Ability ability = new Ability(id, name, effects, cost);
        if (!abilities.containsKey(id) || !abilities.get(id).equals(ability)) {
            abilities.put(id, ability);
        }
        return abilities.get(id);
    }
    public static Ability create(Network.Packets.Fields.AbilityField packetField) {
        Field[] effectIds = packetField.effects.fields;
        Effect[] effects = new Effect[effectIds.length];
        for (int i = 0; i < effectIds.length; i++) {
            effects[i] = Effect.load(((IntegerField) effectIds[i]).value);
        }
        return create(packetField.id.value, packetField.name.value, effects, packetField.cost.value);
    }

    public static Ability[] getAll() {
        Ability[] all = new Ability[abilities.size()];
        Integer[] ids = new Integer[abilities.size()];
        abilities.keySet().toArray(ids);
        for (int i = 0; i < ids.length; i++) {
            all[i] = abilities.get(ids[i]);
        }
        return all;
    }

    public boolean equals(Ability ability) {
        if (effects.length != ability.effects.length)
            return false;
        for (int i = 0; i < effects.length; i++) {
            if (!effects[i].equals(ability.effects[i])) {
                return false;
            }
        }
        return id == ability.id && name.equals(ability.name) && cost == ability.cost;
    }
}
