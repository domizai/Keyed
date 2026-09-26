package ch.domizai.keyed.easing;

public class PowerEasingIn implements Easing {
    private int p = 2;

    public PowerEasingIn() {
    }

    public PowerEasingIn(int p) {
        this.p = p;
    }

    public static float ease(double d, int p) {
        return (float) Math.pow(d, p);
    }

    @Override
    public float apply(float d) {
        return ease(d, p);
    }
}
