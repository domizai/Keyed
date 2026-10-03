package ch.domizai.keyed.tween;

import processing.core.PVector;

/** A {@code Tween<PVector>} with a known arc length, so paths can be chained into a Curve at even speed. */
public interface Path extends Tween<PVector> {
    /** Arc length. */
    float length();

    /** New path between normalized distances t0 and t1 (0..1); t1 < t0 reverses it. */
    Path slice(float t0, float t1);

    /** Curve playing this path, then next. */
    default Curve add(Path next) {
        return new Curve(this, next);
    }

    /** The x coordinate as a tween. */
    default Tween<Float> x() {
        return map(p -> p.x);
    }

    /** The y coordinate as a tween. */
    default Tween<Float> y() {
        return map(p -> p.y);
    }
}
