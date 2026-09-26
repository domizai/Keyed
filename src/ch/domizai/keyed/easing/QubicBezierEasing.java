package ch.domizai.keyed.easing;

import processing.core.PApplet;

public class QubicBezierEasing extends PApplet implements Easing {
    private float p0 = 0f;
    private float p1 = 0f;
    private float p2 = 1f;
    private float p3 = 1f;

    public QubicBezierEasing() {
    }

    public QubicBezierEasing(float p1, float p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    // TODO: Test
    @Override
    public float apply(float d) {
        float a = 1 - d;
        return a * a * a * p0 + 3 * a * a * d * p1 + 3 * a * d * d * p2 + d * d * d * p3;
    }

    // @Override
    // public float apply(float d) {
    //     float c = 3 * (p1 - p0);
    //     float b = 3 * (p2 - p1) - c;
    //     float a = p3 - p0 - c - b;
    //     return a * pow(d, 3) + b * pow(d, 2) + c * d + p0;
    // }
}
