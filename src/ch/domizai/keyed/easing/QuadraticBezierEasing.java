package ch.domizai.keyed.easing;

public class QuadraticBezierEasing implements Easing {
    private float p0 = 0;
    private float p1 = 0;
    private float p2 = 1;

    public QuadraticBezierEasing() {
    }

    public QuadraticBezierEasing(float p0, float p1, float p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    // TODO: Test
    @Override
    public float apply(float d) {
        float a = 1 - d;
        return a * a * p0 + 2 * a * d * p1 + d * d * p2;
    }
}
