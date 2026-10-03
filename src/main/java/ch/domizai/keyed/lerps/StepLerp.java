package ch.domizai.keyed.lerps;

/**
 * Holds a until the blend reaches the threshold, then jumps to b; for values that can't blend.
 * Returns the keys themselves, so only use it with immutable values (enums, Integer, String, ...).
 */
public class StepLerp<T> implements Lerp<T> {
    private final float threshold;

    /** Switches exactly at the next key. */
    public StepLerp() {
        this(1);
    }

    /** E.g. 0.5 switches halfway between keys; easing shifts when that happens. */
    public StepLerp(float threshold) {
        this.threshold = threshold;
    }

    public T lerp(T a, T b, float t) {
        return t >= threshold ? b : a;
    }
}
