package ch.domizai.keyed.lerps;

// Holds a until the blend reaches the threshold, then switches to b.
public class BooleanLerp implements Lerp<Boolean> {
    private final float threshold;

    // Switches exactly at the next key.
    public BooleanLerp() {
        this(1);
    }

    // E.g. 0.5 switches halfway between keys; easing shifts when that happens.
    public BooleanLerp(float threshold) {
        this.threshold = threshold;
    }

    public Boolean lerp(Boolean a, Boolean b, float t) {
        return t >= threshold ? b : a;
    }
}
