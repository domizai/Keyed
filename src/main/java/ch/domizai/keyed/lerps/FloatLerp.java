package ch.domizai.keyed.lerps;

/** Linear blend for floats. */
public class FloatLerp implements Lerp<Float> {
    public Float lerp(Float a, Float b, float d) {
        return a + (b - a) * d;
    }
}
