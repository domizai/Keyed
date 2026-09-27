package ch.domizai.keyed.easing;

import static processing.core.PApplet.constrain;

@FunctionalInterface
public interface Easing {
    float apply(float d); 

    public static final Easing LINEAR = d -> d;
    public static final Easing CUBIC_BEZIER = cubicBezier(0, 1);
    public static final Easing QUADRATIC_BEZIER = quadraticBezier(0, 0, 1);
    public static final Easing SMOOTHSTEP = smoothstep(0, 1);
    public static final Easing STEP = step(0.5f);

    public static final Easing QUAD_IN = powerIn(2);
    public static final Easing QUAD_OUT = powerOut(2);
    public static final Easing QUAD_IN_OUT = powerInOut(2);

    public static final Easing CUBIC_IN = powerIn(3);
    public static final Easing CUBIC_OUT = powerOut(3);
    public static final Easing CUBIC_IN_OUT = powerInOut(3);

    public static final Easing QUART_IN = powerIn(4);
    public static final Easing QUART_OUT = powerOut(4);
    public static final Easing QUART_IN_OUT = powerInOut(4);

    public static final Easing QUINT_IN = powerIn(5);
    public static final Easing QUINT_OUT = powerOut(5);
    public static final Easing QUINT_IN_OUT = powerInOut(5);

    public static Easing powerIn(int p) {
        return d -> (float) Math.pow(d, p);
    }

    public static Easing powerOut(int p) {
        return d -> (float) (1 - Math.pow(1 - d, p));
    }

    public static Easing powerInOut(int p) {
        return d -> (float) (d < 0.5f ? 0.5 * Math.pow(2 * d, p) : 1 - 0.5 * Math.pow(2 - 2 * d, p));
    }

    public static Easing smoothstep(float edge0, float edge1) {
        return d -> {
            float x = constrain((d - edge0) / (edge1 - edge0), 0, 1);
            return x * x * x * (x * (6.0f * x - 15.0f) + 10.0f);
        };
    }

    public static Easing step(float threshold) {
        return d -> d < threshold ? 0 : 1;
    }

    public static Easing staircase(int steps) {
        return d -> (float) Math.round(d * steps) / steps;
    }

    public static Easing cubicBezier(float p1, float p2) {
        return d -> {
            float a = 1 - d;
            return 3 * a * a * d * p1 + 3 * a * d * d * p2 + d * d * d;
        };
    }

    public static Easing quadraticBezier(float p0, float p1, float p2) {
        return d -> {
            float a = 1 - d;
            return a * a * p0 + 2 * a * d * p1 + d * d * p2;
        };
    }
}
