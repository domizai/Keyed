package ch.domizai.keyed.effect;

import ch.domizai.keyed.tween.Tween;

// Holds each pose for `step` timeline units; step 2 with one timeline step per frame animates "on twos".
public class StopMotion<T> implements TimeEffect<T> {
    private final float step;

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
