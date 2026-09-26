package ch.domizai.keyed.easing;

import processing.core.PApplet;

public class SmoothstepEasing extends PApplet implements Easing {
    float edge0 = 0;
    float edge1 = 1;

    public SmoothstepEasing() {
    }

    public SmoothstepEasing(float edge0, float edge1) {
        this.edge0 = edge0;
        this.edge1 = edge1;
    }

    @Override
    public float apply(float d) {
        d = constrain((d - edge0) / (edge1 - edge0), 0, 1);
        return d * d * d * (d * (6.0f * d - 15.0f) + 10.0f);
    }
}
