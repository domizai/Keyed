package ch.domizai.keyed.easing;

@FunctionalInterface
public interface Easing {
    float apply(float d); 

    public static final Easing LINEAR = new LinearEasing();
    public static final Easing QUBIC_BEZIER = new QubicBezierEasing();
    public static final Easing QUARTIC_BEZIER = new QuadraticBezierEasing();

    public static final Easing QUAD_IN = new PowerEasingIn(2);
    public static final Easing QUAD_OUT = new PowerEasingOut(2);
    public static final Easing QUAD_IN_OUT = new PowerEasingInOut(2);

    public static final Easing CUBIC_IN = new PowerEasingIn(3);
    public static final Easing CUBIC_OUT = new PowerEasingOut(3);
    public static final Easing CUBIC_IN_OUT = new PowerEasingInOut(3);

    public static final Easing QUART_IN = new PowerEasingIn(4);
    public static final Easing QUART_OUT = new PowerEasingOut(4);
    public static final Easing QUART_IN_OUT = new PowerEasingInOut(4);

    public static final Easing QUINT_IN = new PowerEasingIn(5);
    public static final Easing QUINT_OUT = new PowerEasingOut(5);
    public static final Easing QUINT_IN_OUT = new PowerEasingInOut(5);
    
    public static Easing powerIn(int p) {
        return new PowerEasingIn(p);
    }

    public static Easing powerOut(int p) {
        return new PowerEasingOut(p);
    }

    public static Easing powerInOut(int p) {
        return new PowerEasingInOut(p);
    }

    public static final Easing SMOOTHSTEP = new SmoothstepEasing();

    public static Easing smoothstep(float a, float b) {
        return new SmoothstepEasing(a, b);
    }

    public static final Easing STEP = new StepEasing();

    public static Easing step(float steps) {
        return new StepEasing(steps);
    }
}
