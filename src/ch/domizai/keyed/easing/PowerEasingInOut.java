package ch.domizai.keyed.easing;

public class PowerEasingInOut implements Easing {
    private int p = 2;

    public PowerEasingInOut() {
    }

    public PowerEasingInOut(int p) {
        this.p = p;
    }

    public static float ease(float d, int p) {
        double c = -Math.pow(-1, p);
        double b = 0.5 * c * Math.pow(d / 0.5 - 2, p) + 1;
        double a = 0.5 * Math.pow(d / 0.5, p);
        double r = Math.round(d);
        return (float)((1 - r) * a + r * b);
    }

    @Override
    public float apply(float d) {
        return ease(d, p);
    }
}
