package ch.domizai.keyed;

import ch.domizai.keyed.effect.Effect;
import ch.domizai.keyed.motion.Motion;
import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.easing.Easing;
import ch.domizai.keyed.easing.PowerEasingInOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import processing.core.PApplet;


public class Keyed<T> extends PApplet {
    private T defaultValue;
    private Map<Key, InOut<T>> keys = new TreeMap<>();
    private Timeline tm = new Timeline();
    private Tween<T> tween;
    private ArrayList<Effect<T>> effects = new ArrayList<>();
    private Easing easing = new PowerEasingInOut(3); 

    public Keyed(Tween<T> tween, T defaultValue) {
        this.tween = tween;
        this.defaultValue = defaultValue;
    }

    public Keyed<T> addKey(float t, T value) {
        keys.put(new Key(t), new InOut<>(value));
        return this;
    }

    public Keyed<T> addKey(Key k, T value) {
        keys.put(k, new InOut<>(value));
        return this;
    }

    public Keyed<T> addKey(Key k, Motion<T> motion) {
        k.addListener(frame -> {
            System.out.println("Keyframe added");
            keys.remove(k);
            keys.put(k, new InOut<>(motion));
        });
        keys.put(k, new InOut<>(motion));
        return this;
    }

    public Keyed<T> setTimeline(Timeline tm) {
        this.tm = tm;
        return this;
    }

    public T value() {
        return value(tm.t());
    }

    public List<Key> keys() {
        return new ArrayList<>(keys.keySet());
    }

    public Timeline timeline() {
        return tm;
    }

    public T value(float t) {
        Iterator<Entry<Key, InOut<T>>> it = keys.entrySet().iterator();

        if (!it.hasNext()) {
            return applyEffects(defaultValue, t);
        }

        Entry<Key, InOut<T>> e1 = it.next();
        InOut<T> k1 = e1.getValue();
        float from = e1.getKey().t();

        if (keys.size() < 2) {
            return applyEffects(from < t ? k1.valueIn(1) : k1.valueOut(0), t);
        }

        Entry<Key, InOut<T>> e2 = it.next();

        while (it.hasNext()) {
            if (t < e2.getKey().t())
                break;
            e1 = e2;
            e2 = it.next();
        }
        
        float to = e2.getKey().t();
        float d = map(constrain(t, from, to), from, to, 0f,1f);
        float e = easing.apply(d);
        float ease = lerp(e1.getKey().easeOut().apply(d), e2.getKey().easeIn().apply(d), e); 
        T r = tween.lerp(k1.valueOut(ease), e2.getValue().valueIn(ease), e);
        return applyEffects(r, t);
    }

    public Keyed<T> addEffect(Effect<T> effect) {
        effects.add(effect);
        return this;
    }

    public T applyEffects(T value, float t) {
        for (Effect<T> effect : effects) {
            value = effect.apply(value, t);
        }
        return value;
    }
}
