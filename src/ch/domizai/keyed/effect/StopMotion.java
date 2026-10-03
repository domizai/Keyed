package ch.domizai.keyed.effect;

import ch.domizai.keyed.tween.Tween;

/** Holds each pose for {@code step} seconds; 2f / 24 animates "on twos" at 24 fps. */
public class StopMotion<T> implements TimeEffect<T> {
    private final float step;

    /** step: hold duration in seconds. */
    public StopMotion(float step) {
        if (step <= 0) {
            throw new IllegalArgumentException("step must be > 0, was " + step);
        }
        this.step = step;
    }

    @Override
    public T apply(Tween<T> source, float t) {
        return source.value((float) Math.floor(t / step) * step);
    }
}
