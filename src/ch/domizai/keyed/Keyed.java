package ch.domizai.keyed;

import ch.domizai.keyed.effect.Effect;
import ch.domizai.keyed.effect.TimeEffect;
import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.tween.TweenAt;
import ch.domizai.keyed.lerp.Lerp;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.constrain;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;


public class Keyed<A> {
    private static PApplet sketch;
    private static Timeline defaultTimeline = new Timeline();

    private A defaultValue;
    private List<KeyEntry<A>> keys = new ArrayList<>();
    // null means defaultTimeline(), looked up on use so Keyeds created before init() still pick it up.
    private Timeline tm;
    private Lerp<A> lerper;
    // raw() wrapped by each added effect in order; the outermost wrapper is the final value.
    private Tween<A> output = this::raw;

    // Call in setup() so timelines follow real time without passing the sketch around.
    public static Timeline init(PApplet sketch) {
        Keyed.sketch = sketch;
        defaultTimeline = new Timeline(sketch);
        return defaultTimeline;
    }

    // null before init().
    public static PApplet sketch() {
        return sketch;
    }

    // Used by Keyed values without their own timeline; before init() it only moves via step() or to().
    public static Timeline defaultTimeline() {
        return defaultTimeline;
    }

    public Keyed(Lerp<A> lerper, A defaultValue) {
        this.lerper = lerper;
        this.defaultValue = copy(defaultValue);
    }

    public Keyed<A> key(Key k, Tween<A> tween) {
        return key(k, new TweenAt<>(tween));
    }

    public Keyed<A> key(Pin f, Tween<A> tween) {
        return key(Key.at(f), tween);
    }

    public Keyed<A> key(Key k, TweenAt<A> tweenAt) {
        for (KeyEntry<A> e : keys) {
            if (e.key == k) {
                e.tween = tweenAt;
                return this;
            }
        }
        keys.add(new KeyEntry<>(k, tweenAt));
        return this;
    }
    
    public Keyed<A> key(Pin f, TweenAt<A> tweenAt) {
        return key(Key.at(f), tweenAt);
    }

    public Keyed<A> key(Key k, A value) {
        A v = copy(value);
        return key(k, new TweenAt<>(d -> v));
    }
    
    public Keyed<A> key(Pin f, A value) {
        return key(Key.at(f), value);
    }

    public Keyed<A> setTimeline(Timeline tm) {
        this.tm = tm;
        return this;
    }

    public A value() {
        return value(timeline().t());
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
        return tm != null ? tm : defaultTimeline;
    }

    public A value(float t) {
        return output.value(t);
    }

    private A raw(float t) {
        sortKeys();
        int n = keys.size();

        if (n == 0) {
            return copy(defaultValue);
        }

        if (n == 1) {
            TweenAt<A> k = keys.get(0).tween;
            return copy(k.tween().value(k.positionOr(0)));
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
        float ease = e2.key.easing().apply(d);
        float position = lerp(e1.tween.positionOr(0), e2.tween.positionOr(1), ease);
        Tween<A> w0 = e1.tween.tween();
        Tween<A> w1 = e2.tween.tween();
        A v0 = w0.value(position);
        // Same tween at the same position gives the same value; blending it with itself is a copy.
        return w0 == w1 ? copy(v0) : lerper.lerp(v0, w1.value(position), ease);
    }

    public Keyed<A> addEffect(TimeEffect<A> effect) {
        Tween<A> source = output;
        output = s -> effect.apply(source, s);
        return this;
    }

    // More specific than the TimeEffect overload, so lambdas like (v, t) -> ... get a value, not a source.
    public Keyed<A> addEffect(Effect<A> effect) {
        return addEffect((TimeEffect<A>) effect);
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
