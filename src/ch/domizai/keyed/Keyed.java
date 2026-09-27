package ch.domizai.keyed;

import ch.domizai.keyed.effect.Effect;
import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.tween.TweenAt;
import ch.domizai.keyed.lerp.Lerp;
import ch.domizai.keyed.easing.Easing;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.constrain;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;


public class Keyed<A> {
    private A defaultValue;
    private List<KeyEntry<A>> keys = new ArrayList<>();
    private Timeline tm = new Timeline();
    private Lerp<A> lerper;
    private ArrayList<Effect<A>> effects = new ArrayList<>();
    private Easing easing = Easing.CUBIC_IN_OUT; // TODO: add setter

    public Keyed(Lerp<A> lerper, A defaultValue) {
        this.lerper = lerper;
        this.defaultValue = copy(defaultValue);
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
        A v = copy(value);
        return addKey(k, new TweenAt<>(d -> v));
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
            return applyEffects(copy(defaultValue), t);
        }

        if (n == 1) {
            TweenAt<A> k = keys.get(0).tween;
            return applyEffects(copy(k.tween().value(k.positionOr(0))), t);
        }

        // First key after t, limited to [1, n - 1] so t outside the keys uses the first or last segment.
        int lo = 1, hi = n - 1;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (t < keys.get(mid).key.t()) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        KeyEntry<A> e1 = keys.get(lo - 1);
        KeyEntry<A> e2 = keys.get(lo);

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

    // lerp(v, v, 0) returns a new value, so callers can't mutate stored keys through it.
    private A copy(A value) {
        return lerper.lerp(value, value, 0);
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
