package Network.dataStructures;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class Effect {
    private static final HashMap<Integer, Effect> effects = new HashMap<>();

    public final int id;
    public final String name;
    public final float min;
    public final float max;
    public final int duration;
    public final int valueAffected;
    public final boolean relative;
    public final boolean hitSelf;

    private Effect(int id, String name, float min, float max, int duration, int valueAffected, boolean relative, boolean hitSelf) {
        this.id = id;
        this.name = name;
        this.min = min;
        this.max = max;
        this.duration = duration;
        this.valueAffected = valueAffected;
        this.relative = relative;
        this.hitSelf = hitSelf;
    }
    public static Effect load(int id) throws NoSuchElementException {
        if (!effects.containsKey(id)) {
            throw new NoSuchElementException(String.format("Effect with id %d does not exist", id));
        }
        return effects.get(id);
    }
    public static Effect create(int id, String name, float min, float max, int duration, int valueAffected, boolean relative, boolean hitSelf) {
        Effect effect = new Effect(id, name, min, max, duration, valueAffected, relative, hitSelf);
        if (!effects.containsKey(id) || !effects.get(id).equals(effect)) {
            effects.put(id, effect);
        }
        return effects.get(id);
    }
    public static Effect load(int id, String name, float min, float max, int duration, int valueAffected, boolean relative, boolean hitSelf) {
        if (effects.containsKey(id)) {
            return effects.get(id);
        }
        return create(id, name, min, max, duration, valueAffected, relative, hitSelf);
    }
    public static Effect create(Network.Packets.Fields.EffectField effectField) {
        return create(effectField.id, effectField.name, effectField.min, effectField.max, effectField.time, effectField.valueAffected, effectField.isPercent, effectField.hitsSelf);
    }
    public static Effect load(Network.Packets.Fields.EffectField effectField) {
        if (effects.containsKey(effectField.id.value)) {
            return effects.get(effectField.id.value);
        }
        return create(effectField);
    }

    public static Effect[] getAll() {
        Effect[] all = new Effect[effects.size()];
        Integer[] ids = new Integer[effects.size()];
        effects.keySet().toArray(ids);
        for (int i = 0; i < ids.length; i++) {
            all[i] = effects.get(ids[i]);
        }
        return all;
    }

    public boolean equals(Effect effect) {
        return id == effect.id &&
                name.equals(effect.name) &&
                min == effect.min && max == effect.max &&
                duration == effect.duration &&
                valueAffected == effect.valueAffected &&
                relative == effect.relative &&
                hitSelf == effect.hitSelf;
    }
}
