package ch.domizai.keyed;

import processing.core.PApplet;
import processing.core.PVector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.domizai.keyed.effect.Effect;
import ch.domizai.keyed.effect.TimeEffect;
import ch.domizai.keyed.lerps.BooleanLerp;
import ch.domizai.keyed.lerps.ColorLerp;
import ch.domizai.keyed.lerps.FloatLerp;
import ch.domizai.keyed.lerps.IntLerp;
import ch.domizai.keyed.lerps.Lerp;
import ch.domizai.keyed.lerps.PVectorLerp;
import ch.domizai.keyed.lerps.StringLerp;
import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.tween.TweenAt;
import ch.domizai.keyed.types.Lerpable;

import static processing.core.PApplet.constrain;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;

/** A value animated between keys, plus the library-wide settings as static methods. */
public class Keyed<A> {
    /** Library version, e.g. "1.1.0"; "dev" when running from source instead of the jar. */
    public static final String VERSION = versionOrDev();

    public static final Unit SECOND = Unit.SECOND;
    public static final Unit FRAME = Unit.FRAME;

    private static PApplet sketch;
    // Declared before defaultTimeline, which reads it while this class initializes.
    private static Unit unit = Unit.SECOND;
    private static boolean synced = true;
    private static boolean autoplay = true;
    private static float frameRate = 60;
    private static Timeline defaultTimeline = new Timeline();
    // Registered with the sketch once; steps the timelines before applying bound values, in that order.
    private static final Binder binder = new Binder();

    private A defaultValue;
    private List<KeyEntry<A>> keys = new ArrayList<>();
    // null means defaultTimeline(), looked up on use so values created before init() still pick it up.
    private Timeline tm;
    private Lerp<A> lerper;
    // raw() wrapped by each added effect in order; the outermost wrapper is the final value.
    private Tween<A> output = this::raw;
    private Consumer<A> target;

    /** Call in setup() so timelines follow real time without passing the sketch around. */
    public static Timeline init(PApplet sketch) {
        if (Keyed.sketch != sketch) {
            if (Keyed.sketch != null) {
                Keyed.sketch.unregisterMethod("pre", binder);
            }
            sketch.registerMethod("pre", binder);
        }
        Keyed.sketch = sketch;
        defaultTimeline.dispose();
        defaultTimeline = new Timeline(sketch);
        return defaultTimeline;
    }

    static Binder binder() {
        return binder;
    }

    /** Sketch passed to init(); null before. */
    public static PApplet sketch() {
        return sketch;
    }

    /** Unit for the default timeline and new timelines. */
    public static Unit unit() {
        return unit;
    }

    /** Sets the unit of the default timeline and of timelines created afterwards. */
    public static void setUnit(Unit unit) {
        Keyed.unit = unit;
        defaultTimeline.setUnit(unit);
    }

    /** Sync setting for new timelines. */
    public static boolean isSynced() {
        return synced;
    }

    /** Sets sync for the default timeline and timelines created afterwards; see Timeline.sync(). */
    public static void sync(boolean sync) {
        Keyed.synced = sync;
        defaultTimeline.sync(sync);
    }

    /** Autoplay setting for new timelines. */
    public static boolean isAutoplay() {
        return autoplay;
    }

    /** Sets autoplay for the default timeline and timelines created afterwards; see Timeline.autoplay(). */
    public static void autoplay(boolean autoplay) {
        Keyed.autoplay = autoplay;
        defaultTimeline.autoplay(autoplay);
    }

    /** Frame rate used to convert between frames and seconds. */
    public static float frameRate() {
        return frameRate;
    }

    /** Must be &gt; 0; pass the same value as Processing's frameRate(), which isn't exposed. */
    public static void setFrameRate(float fps) {
        if (fps <= 0) {
            throw new IllegalArgumentException("frame rate must be > 0, was " + fps);
        }
        Keyed.frameRate = fps;
    }

    /** Used by Keyed values without their own timeline; before init() it only moves via step() or to(). */
    public static Timeline defaultTimeline() {
        return defaultTimeline;
    }

    /** Blends keys with lerper; defaultValue is used while there are no keys. */
    public Keyed(Lerp<A> lerper, A defaultValue) {
        this.lerper = lerper;
        this.defaultValue = copy(defaultValue);
    }

    /** For types that know how to blend themselves, e.g. Keyed.of(new Transform(0, 0)). */
    public static <A extends Lerpable<A>> Keyed<A> of(A defaultValue) {
        return new Keyed<>(A::lerp, defaultValue);
    }

    /** Animated float. */
    public static Keyed<Float> of(float defaultValue) {
        return new Keyed<>(new FloatLerp(), defaultValue);
    }

    /** Animated PVector. */
    public static Keyed<PVector> of(PVector defaultValue) {
        return new Keyed<>(new PVectorLerp(), defaultValue);
    }

    /** Animated String. */
    public static Keyed<String> of(String defaultValue) {
        return new Keyed<>(new StringLerp(), defaultValue);
    }

    /** Animated boolean. */
    public static Keyed<Boolean> of(boolean defaultValue) {
        return new Keyed<>(new BooleanLerp(), defaultValue);
    }

    // Named factories: the type is in the name, so the no-argument versions need no default value,
    // which is only used while there are no keys.

    /** Animated float, 0 while there are no keys. */
    public static Keyed<Float> ofFloat() {
        return ofFloat(0);
    }

    /** Same as of(float). */
    public static Keyed<Float> ofFloat(float defaultValue) {
        return of(defaultValue);
    }

    /** Animated int, 0 while there are no keys. */
    public static Keyed<Integer> ofInt() {
        return ofInt(0);
    }

    /** Animated int; named because of(int) would clash with of(float). */
    public static Keyed<Integer> ofInt(int defaultValue) {
        return new Keyed<>(new IntLerp(), defaultValue);
    }

    /** Animated color, opaque black while there are no keys. */
    public static Keyed<Integer> ofColor() {
        return ofColor(0xFF000000);
    }

    /** Animated color; colors are ints too, hence the name. */
    public static Keyed<Integer> ofColor(int defaultValue) {
        return new Keyed<>(new ColorLerp(), defaultValue);
    }

    /** Animated boolean, false while there are no keys. */
    public static Keyed<Boolean> ofBoolean() {
        return ofBoolean(false);
    }

    /** Same as of(boolean). */
    public static Keyed<Boolean> ofBoolean(boolean defaultValue) {
        return of(defaultValue);
    }

    /** Animated String, empty while there are no keys. */
    public static Keyed<String> ofString() {
        return ofString("");
    }

    /** Same as of(String). */
    public static Keyed<String> ofString(String defaultValue) {
        return of(defaultValue);
    }

    /** Animated PVector, (0, 0, 0) while there are no keys. */
    public static Keyed<PVector> ofPVector() {
        return ofPVector(new PVector());
    }

    /** Same as of(PVector). */
    public static Keyed<PVector> ofPVector(PVector defaultValue) {
        return of(defaultValue);
    }

    /** The setter receives value() before every draw() once init() has been called. */
    public static <A> Keyed<A> bind(Lerp<A> lerper, A defaultValue, Consumer<A> setter) {
        return new Keyed<>(lerper, defaultValue).bind(setter);
    }

    /** Binds a float or Float field by name; its current value becomes the default. */
    public static Keyed<Float> bind(Object target, String fieldName) {
        Field field = findField(target.getClass(), fieldName);
        Class<?> type = field.getType();
        if (type != float.class && type != Float.class) {
            throw new IllegalArgumentException("Field '" + fieldName + "' is " + type.getSimpleName() + ", only float can be bound");
        }
        if (Modifier.isFinal(field.getModifiers())) {
            throw new IllegalArgumentException("Field '" + fieldName + "' is final");
        }
        field.setAccessible(true);
        try {
            Float current = (Float) field.get(target);
            return bind(new FloatLerp(), current != null ? current : 0f, v -> {
                try {
                    field.set(target, v);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /** The setter receives value() before every draw() once init() has been called; replaces any previous one. */
    public Keyed<A> bind(Consumer<A> setter) {
        target = setter;
        binder.add(this);
        return this;
    }

    /** Stops updating the bound setter. */
    public Keyed<A> unbind() {
        target = null;
        binder.remove(this);
        return this;
    }

    /** Writes value() to the bound target now, e.g. in setup() before the first pre(). */
    public Keyed<A> apply() {
        if (target != null) target.accept(value());
        return this;
    }

    /** Adds key k whose value follows tween. */
    public Keyed<A> key(Key k, Tween<A> tween) {
        return key(k, new TweenAt<>(tween));
    }

    /** Adds a key on pin p whose value follows tween. */
    public Keyed<A> key(Pin p, Tween<A> tween) {
        return key(Key.at(p), tween);
    }

    /** Adds key k, or replaces its tween if k was already added. */
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

    /** Adds a key on pin p. */
    public Keyed<A> key(Pin p, TweenAt<A> tweenAt) {
        return key(Key.at(p), tweenAt);
    }

    /** Adds key k holding a copy of value. */
    public Keyed<A> key(Key k, A value) {
        A v = copy(value);
        return key(k, new TweenAt<>(d -> v));
    }

    /** Adds a key on pin p holding a copy of value. */
    public Keyed<A> key(Pin p, A value) {
        return key(Key.at(p), value);
    }

    /** Adds a key at time t whose value follows tween. */
    public Keyed<A> key(float t, Tween<A> tween) {
        return key(Key.at(t), tween);
    }

    /** Adds a key at time t. */
    public Keyed<A> key(float t, TweenAt<A> tweenAt) {
        return key(Key.at(t), tweenAt);
    }

    /** Adds a key at time t holding a copy of value. */
    public Keyed<A> key(float t, A value) {
        return key(Key.at(t), value);
    }

    /** Removes key k. */
    public Keyed<A> removeKey(Key k) {
        keys.removeIf(e -> e.key == k);
        return this;
    }

    /** Removes every key placed on this pin. */
    public Keyed<A> removeKey(Pin p) {
        keys.removeIf(e -> e.key.pin() == p);
        return this;
    }

    /** Removes all keys. */
    public Keyed<A> clearKeys() {
        keys.clear();
        return this;
    }

    /** Follows tm instead of the default timeline; null reverts to the default. */
    public Keyed<A> setTimeline(Timeline tm) {
        this.tm = tm;
        return this;
    }

    /** Value at the timeline's current time, with effects applied. */
    public A value() {
        return value(timeline().t());
    }

    /** Value at time t, with effects applied. */
    public A value(float t) {
        return output.value(t);
    }

    /** Keys sorted by time. */
    public List<Key> keys() {
        sortKeys();
        List<Key> list = new ArrayList<>(keys.size());
        for (KeyEntry<A> e : keys) {
            list.add(e.key);
        }
        return list;
    }

    /** Timeline set with setTimeline(), otherwise the default timeline. */
    public Timeline timeline() {
        return tm != null ? tm : defaultTimeline;
    }

    /** Wraps the current output; effects apply in the order added. On a looping timeline, times they sample outside the duration wrap around. */
    public Keyed<A> addEffect(TimeEffect<A> effect) {
        Tween<A> source = output;
        Tween<A> wrapped = s -> source.value(wrap(s));
        output = s -> effect.apply(wrapped, s);
        return this;
    }

    /** More specific than the TimeEffect overload, so lambdas like (v, t) -&gt; ... get a value, not a source. */
    public Keyed<A> addEffect(Effect<A> effect) {
        return addEffect((TimeEffect<A>) effect);
    }

    // On a looping timeline, times an effect samples outside [0, duration] wrap around like playback,
    // so effects looking into the past (Spring, Lag) continue seamlessly across the loop.
    private float wrap(float s) {
        Timeline tm = timeline();
        float d = tm.duration();
        if (!tm.isLooping() || d <= 0 || (s >= 0 && s <= d)) {
            return s;
        }
        return (s % d + d) % d;
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
        Easing preset = e1.key.easing();
        float ease = preset != null
            ? preset.apply(d)
            : Easing.cubicBezier(e1.key.easingOut(), 0, 1 - e2.key.easingIn(), 1).apply(d);
        float position = lerp(e1.tween.positionOr(0), e2.tween.positionOr(1), ease);
        Tween<A> w0 = e1.tween.tween();
        Tween<A> w1 = e2.tween.tween();
        A v0 = w0.value(position);
        // Same tween at the same position gives the same value; blending it with itself is a copy.
        return w0 == w1 ? copy(v0) : lerper.lerp(v0, w1.value(position), ease);
    }

    // lerp(v, v, 0) returns a new value, so callers can't mutate stored keys through it.
    private A copy(A value) {
        return lerper.lerp(value, value, 0);
    }

    // Keys are mutable (Key.to, shared Pins), so order is restored on read; stable for equal times.
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

    // The jar's manifest holds the version written by the build; classes run from source have none.
    private static String versionOrDev() {
        String v = Keyed.class.getPackage().getImplementationVersion();
        return v != null ? v : "dev";
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new IllegalArgumentException("No field '" + name + "' on " + type.getName());
    }

    /** Values at now, now - delay, now - 2 * delay, ...; a negative delay samples the future. */
    public List<A> echo(int samples, float delay) {
        Timeline timeline = timeline();
        List<A> values = new ArrayList<>();
        for (int i = 0; i < samples; i++)
            values.add(value(timeline.t(-delay * i)));
        return values;
    }
}
