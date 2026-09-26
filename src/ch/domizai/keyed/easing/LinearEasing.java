package ch.domizai.keyed.easing;

public class LinearEasing implements Easing {
    @Override
    public float apply(float d) {
        return d;
    }

    public static float ease(float d) {
        return d;
    }
}
