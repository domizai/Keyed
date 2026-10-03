package ch.domizai.keyed.lerps;

public class FloatLerp implements Lerp<Float> {
    public Float lerp(Float a, Float b, float d) {
        return a + (b - a) * d;
    }
}
