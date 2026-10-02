package ch.domizai.keyed;

import processing.core.PApplet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import ch.domizai.keyed.types.FloatLerp;
import ch.domizai.keyed.types.Lerp;

// Library-wide settings, plus a keyed value for any type given its Lerp; see KeyedBase for the instance API.
public class Keyed<A> extends KeyedBase<A, Keyed<A>> {
    public static final Unit SECOND = Unit.SECOND;
    public static final Unit FRAME = Unit.FRAME;

    private static PApplet sketch;
    // Declared before defaultTimeline, which reads it while this class initializes.
    private static Unit unit = Unit.SECOND;
    private static boolean synced = true;
    private static boolean autoplay = true;
    private static float frameRate = 60;
    private static Timeline defaultTimeline = new Timeline();
    static final Binder binder = new Binder(); // Used to register the pre() method with the Processing sketch.

    // Call in setup() so timelines follow real time without passing the sketch around.
    public static Timeline init(PApplet sketch) {
        if (Keyed.sketch != sketch) {
            sketch.registerMethod("pre", binder);
        }
        Keyed.sketch = sketch;
        defaultTimeline = new Timeline(sketch);
        return defaultTimeline;
    }

    // null before init().
    public static PApplet sketch() {
        return sketch;
    }

    public static Unit unit() {
        return unit;
    }

    // Sets the unit of the default timeline and of timelines created afterwards.
    public static void setUnit(Unit unit) {
        Keyed.unit = unit;
        defaultTimeline.setUnit(unit);
    }

    public static boolean isSynced() {
        return synced;
    }

    // Sets sync for the default timeline and timelines created afterwards; see Timeline.sync().
    public static void sync(boolean sync) {
        Keyed.synced = sync;
        defaultTimeline.sync(sync);
    }

    public static boolean isAutoplay() {
        return autoplay;
    }

    // Sets autoplay for the default timeline and timelines created afterwards; see Timeline.autoplay().
    public static void autoplay(boolean autoplay) {
        Keyed.autoplay = autoplay;
        defaultTimeline.autoplay(autoplay);
    }

    public static float frameRate() {
        return frameRate;
    }

    // Processing doesn't expose its target frame rate, so pass the same value as frameRate().
    public static void setFrameRate(float fps) {
        if (fps <= 0) {
            throw new IllegalArgumentException("frame rate must be > 0, was " + fps);
        }
        Keyed.frameRate = fps;
    }

    // Used by Keyed values without their own timeline; before init() it only moves via step() or to().
    public static Timeline defaultTimeline() {
        return defaultTimeline;
    }

    public Keyed(Lerp<A> lerper, A defaultValue) {
        super(lerper, defaultValue);
    }

    @Override
    protected Keyed<A> self() {
        return this;
    }

    // The setter receives value() before every draw() once init() has been called.
    public static <A> Keyed<A> bind(Lerp<A> lerper, A defaultValue, Consumer<A> setter) {
        return new Keyed<>(lerper, defaultValue).bind(setter);
    }

    // Binds a float or Float field by name; its current value becomes the default.
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

    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new IllegalArgumentException("No field '" + name + "' on " + type.getName());
    }
}
