package ch.domizai.keyed.types;

public class FloatLerp implements Lerp<Float> {
    public Float lerp(Float a, Float b, float d) {
        return a + (b - a) * d;
    }
}
