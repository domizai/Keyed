package ch.domizai.keyed;

import static processing.core.PApplet.constrain;

@FunctionalInterface
public interface Easing {
    float apply(float d); 

    public static final Easing LINEAR = d -> d;
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

    // CSS cubic-bezier(x1, y1, x2, y2) from (0, 0) to (1, 1); y outside [0, 1] overshoots like CSS "back" easings.
    public static Easing cubicBezier(float x1, float y1, float x2, float y2) {
        if (x1 < 0 || x1 > 1 || x2 < 0 || x2 > 1) {
            throw new IllegalArgumentException("x1 and x2 must be in [0, 1], were " + x1 + ", " + x2);
        }
        float cx = 3 * x1, bx = 3 * (x2 - x1) - cx, ax = 1 - cx - bx;
        float cy = 3 * y1, by = 3 * (y2 - y1) - cy, ay = 1 - cy - by;
        return d -> {
            float s = solveBezierX(d, ax, bx, cx);
            return ((ay * s + by) * s + cy) * s;
        };
    }

    // Curve parameter s in [0, 1] where ((ax*s + bx)*s + cx)*s == x: Newton's method, bisection if it stalls or leaves [0, 1].
    private static float solveBezierX(float x, float ax, float bx, float cx) {
        if (x <= 0) {
            return 0;
        }
        if (x >= 1) {
            return 1;
        }
        float s = x;
        for (int i = 0; i < 8; i++) {
            float err = ((ax * s + bx) * s + cx) * s - x;
            if (Math.abs(err) < 1e-6f) {
                return s;
            }
            float slope = (3 * ax * s + 2 * bx) * s + cx;
            if (Math.abs(slope) < 1e-6f) {
                break;
            }
            s -= err / slope;
            if (s < 0 || s > 1) {
                break;
            }
        }
        float lo = 0, hi = 1;
        s = x;
        for (int i = 0; i < 40; i++) {
            float xs = ((ax * s + bx) * s + cx) * s;
            if (Math.abs(xs - x) < 1e-6f) {
                break;
            }
            if (xs < x) {
                lo = s;
            } else {
                hi = s;
            }
            s = (lo + hi) / 2;
        }
        return s;
    }

    public static Easing quadraticBezier(float p0, float p1, float p2) {
        return d -> {
            float a = 1 - d;
            return a * a * p0 + 2 * a * d * p1 + d * d * p2;
        };
    }
}
