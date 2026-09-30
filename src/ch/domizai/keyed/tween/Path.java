package ch.domizai.keyed.tween;

import processing.core.PVector;

// A Tween<PVector> with a known arc length, so paths can be chained into a Curve at even speed.
public interface Path extends Tween<PVector> {
    float length();

    Path slice(float t0, float t1);

    default Curve add(Path next) {
        return new Curve(this, next);
    }

    default Tween<Float> x() {
        return map(p -> p.x);
    }

    default Tween<Float> y() {
        return map(p -> p.y);
    }
}
