package ch.domizai.keyed;

import ch.domizai.keyed.effect.Effect;
import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.tween.TweenAt;
import ch.domizai.keyed.lerps.Lerp;
import ch.domizai.keyed.easing.Easing;
import ch.domizai.keyed.easing.PowerEasingInOut;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;


public class Keyed<A> extends PApplet {
    private Tween<A> defaultTween;
    private List<KeyEntry<A>> keys = new ArrayList<>();
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
        for (KeyEntry<A> e : keys) {
            if (e.key == k) {
                e.tween = tweenAt;
                return this;
            }
        }
        keys.add(new KeyEntry<>(k, tweenAt));
        return this;
    }
    
    public Keyed<A> addKey(Key k, A value) {
        Tween<A> clone = defaultTween.clone();
        clone.set(value);
        return addKey(k, new TweenAt<>(clone));
    }

    public Keyed<A> setTimeline(Timeline tm) {
        this.tm = tm;
        return this;
    }

    public A value() {
        return value(tm.t());
    }

    public List<Key> keys() {
        sortKeys();
        List<Key> list = new ArrayList<>(keys.size());
        for (KeyEntry<A> e : keys) {
            list.add(e.key);
        }
        return list;
    }

    public Timeline timeline() {
        return tm;
    }

    public A value(float t) {
        sortKeys();
        int n = keys.size();

        if (n == 0) {
            return applyEffects(defaultTween.value(0), t);
        }

        if (n == 1) {
            TweenAt<A> k = keys.get(0).tween;
            return applyEffects(k.tween().value(k.positionOr(0)), t);
        }

        int i = 1;
        while (i < n - 1 && t >= keys.get(i).key.t()) {
            i++;
        }
        KeyEntry<A> e1 = keys.get(i - 1);
        KeyEntry<A> e2 = keys.get(i);

        float from = e1.key.t();
        float to = e2.key.t();
        float d = to > from ? map(constrain(t, from, to), from, to, 0f, 1f) : (t < to ? 0f : 1f);
        float e = easing.apply(d);
        float ease = lerp(e1.key.easeOut().apply(d), e2.key.easeIn().apply(d), e); 
        float position = lerp(e1.tween.positionOr(0), e2.tween.positionOr(1), ease);
        A v0 = e1.tween.tween().value(position);
        A v1 = e2.tween.tween().value(position);
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

    // Keys are mutable (Key.to, shared Frames), so order is restored on read; stable for equal times.
    private void sortKeys() {
        keys.sort((a, b) -> a.key.compareTo(b.key));
    }

    private static final class KeyEntry<A> {
        final Key key;
        TweenAt<A> tween;

        KeyEntry(Key key, TweenAt<A> tween) {
            this.key = key;
            this.tween = tween;
        }
    }
}
