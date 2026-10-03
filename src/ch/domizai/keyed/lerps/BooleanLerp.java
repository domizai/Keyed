package ch.domizai.keyed.lerps;

// A StepLerp for booleans.
public class BooleanLerp extends StepLerp<Boolean> {
    /** Switches exactly at the next key. */
    public BooleanLerp() {
        super();
    }

    /** E.g. 0.5 switches halfway between keys; easing shifts when that happens. */
    public BooleanLerp(float threshold) {
        super(threshold);
    }
}
