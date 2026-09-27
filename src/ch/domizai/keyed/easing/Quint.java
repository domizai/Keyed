package ch.domizai.keyed.easing;

public class Quint {
    public static float in(float d) {
        return PowerEasingIn.ease(d, 5);
    }

    public static float out(float d) {
        return PowerEasingOut.ease(d, 5);
    }

    public static float inOut(float d) {
        return PowerEasingInOut.ease(d, 5);
    }
}
