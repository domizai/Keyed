package ch.domizai.keyed.easing;

public class PowerEasingOut implements Easing {
    private int p = 2;

    public PowerEasingOut() {
    }

    public PowerEasingOut(int p) {
        this.p = p;
    }

    public static float ease(float d, int p) {
        return (float) (1 - Math.pow(1 - d, p));
    }

    @Override
    public float apply(float d) {
        return ease(d, p);
    }
}
