package ch.domizai.keyed;

import ch.domizai.keyed.effect.Effect;
import ch.domizai.keyed.effect.TimeEffect;
import ch.domizai.keyed.lerp.Lerp;
import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.tween.TweenAt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static processing.core.PApplet.constrain;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;

// S is the subclass itself, so chained calls return the subclass type. Extend as: class X extends KeyedBase<T, X>.
public abstract class KeyedBase<A, S extends KeyedBase<A, S>> {
    private A defaultValue;
    private List<KeyEntry<A>> keys = new ArrayList<>();
    // null means Keyed.defaultTimeline(), looked up on use so values created before init() still pick it up.
    private Timeline tm;
    private Lerp<A> lerper;
    // raw() wrapped by each added effect in order; the outermost wrapper is the final value.
    private Tween<A> output = this::raw;
    private Consumer<A> target;

    protected KeyedBase(Lerp<A> lerper, A defaultValue) {
        this.lerper = lerper;
        this.defaultValue = copy(defaultValue);
    }

    protected abstract S self();

    // The setter receives value() before every draw() once Keyed.init() has been called.
    public S bind(Consumer<A> setter) {
        target = setter;
        Keyed.binder.add(this);
        return self();
    }

    public S unbind() {
        target = null;
        Keyed.binder.remove(this);
        return self();
    }

    // Writes value() to the bound target now, e.g. in setup() before the first pre().
    public S apply() {
        if (target != null) target.accept(value());
        return self();
    }

    public S key(Key k, Tween<A> tween) {
        return key(k, new TweenAt<>(tween));
    }

    public S key(Pin f, Tween<A> tween) {
        return key(Key.at(f), tween);
    }

    public S key(Key k, TweenAt<A> tweenAt) {
        for (KeyEntry<A> e : keys) {
            if (e.key == k) {
                e.tween = tweenAt;
                return self();
            }
        }
        keys.add(new KeyEntry<>(k, tweenAt));
        return self();
    }

    public S key(Pin f, TweenAt<A> tweenAt) {
        return key(Key.at(f), tweenAt);
    }

    public S key(Key k, A value) {
        A v = copy(value);
        return key(k, new TweenAt<>(d -> v));
    }

    public S key(Pin f, A value) {
        return key(Key.at(f), value);
    }

    public S key(float t, Tween<A> tween) {
        return key(Key.at(t), tween);
    }

    public S key(float t, TweenAt<A> tweenAt) {
        return key(Key.at(t), tweenAt);
    }

    public S key(float t, A value) {
        return key(Key.at(t), value);
    }

    public S setTimeline(Timeline tm) {
        this.tm = tm;
        return self();
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
        return tm != null ? tm : Keyed.defaultTimeline();
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
        float ease = Easing.cubicBezier(e1.key.easingOut(), 0, 1 - e2.key.easingIn(), 1).apply(d);
        float position = lerp(e1.tween.positionOr(0), e2.tween.positionOr(1), ease);
        Tween<A> w0 = e1.tween.tween();
        Tween<A> w1 = e2.tween.tween();
        A v0 = w0.value(position);
        // Same tween at the same position gives the same value; blending it with itself is a copy.
        return w0 == w1 ? copy(v0) : lerper.lerp(v0, w1.value(position), ease);
    }

    public S addEffect(TimeEffect<A> effect) {
        Tween<A> source = output;
        output = s -> effect.apply(source, s);
        return self();
    }

    // More specific than the TimeEffect overload, so lambdas like (v, t) -> ... get a value, not a source.
    public S addEffect(Effect<A> effect) {
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
