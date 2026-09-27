package ch.domizai.keyed;

import ch.domizai.keyed.effect.Effect;
import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.tween.TweenAt;
import ch.domizai.keyed.lerps.Lerp;
import ch.domizai.keyed.easing.Easing;
import ch.domizai.keyed.easing.PowerEasingInOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import processing.core.PApplet;


public class Keyed<A> extends PApplet {
    private Tween<A> defaultTween;
    private Map<Key, TweenAt<A>> keys = new TreeMap<>();
    private Timeline tm = new Timeline();
    private Lerp<A> lerper;
    private ArrayList<Effect<A>> effects = new ArrayList<>();
    private Easing easing = new PowerEasingInOut(3); 

    public Keyed(Lerp<A> lerper, Tween<A> defaultTween) {
        this.lerper = lerper;
        this.defaultTween = defaultTween;
    }

    public Keyed<A> addKey(Key k, Tween<A> tween) {
        return addKey(k, new TweenAt<>(tween));
    }

    public Keyed<A> addKey(Key k, TweenAt<A> tweenAt) {
        keys.put(k, tweenAt);
        return this;
    }
    
    public Keyed<A> addKey(Key k, A value) {
        Tween<A> clone = defaultTween.clone();
        clone.set(value);
        keys.put(k, new TweenAt<>(clone));
        return this;
    }

    public Keyed<A> setTimeline(Timeline tm) {
        this.tm = tm;
        return this;
    }

    public A value() {
        return value(tm.t());
    }

    public List<Key> keys() {
        return new ArrayList<>(keys.keySet());
    }

    public Timeline timeline() {
        return tm;
    }

    public A value(float t) {
        Iterator<Entry<Key, TweenAt<A>>> it = keys.entrySet().iterator();

        if (!it.hasNext()) {
            return applyEffects(defaultTween.value(0), t);
        }

        Entry<Key, TweenAt<A>> e1 = it.next();
        TweenAt<A> k1 = e1.getValue();
        float from = e1.getKey().t();

        if (keys.size() < 2) {
            return applyEffects(k1.tween().value(k1.positionOr(0)), t);
        }

        Entry<Key, TweenAt<A>> e2 = it.next();

        while (it.hasNext()) {
            if (t < e2.getKey().t())
                break;
            e1 = e2;
            e2 = it.next();
        }

        from = e1.getKey().t();
        float to = e2.getKey().t();
        float d = map(constrain(t, from, to), from, to, 0f,1f);
        float e = easing.apply(d);
        float ease = lerp(e1.getKey().easeOut().apply(d), e2.getKey().easeIn().apply(d), e); 
        float position = lerp(e1.getValue().positionOr(0), e2.getValue().positionOr(1), ease);
        A v0 = e1.getValue().tween().value(position);
        A v1 = e2.getValue().tween().value(position);
        A r = lerper.lerp(v0, v1, ease);
        return applyEffects(r, t);
    }

    public Keyed<A> addEffect(Effect<A> effect) {
        effects.add(effect);
        return this;
    }

    public A applyEffects(A value, float t) {
        for (Effect<A> effect : effects) {
            value = effect.apply(value, t);
        }
        return value;
    }
}
