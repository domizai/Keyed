package ch.domizai.keyed.effect;

import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.types.Lerp;

// Averages the value over the last `duration` seconds: trails behind and smooths out sharp moves, without overshoot.
public class Lag<T> implements TimeEffect<T> {
    private final Lerp<T> lerper;
    private final float duration;
    private final int samples;

    // samples: more is smoother but costs one source evaluation each.
    public Lag(Lerp<T> lerper, float duration, int samples) {
        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be > 0, was " + duration);
        }
        if (samples < 1) {
            throw new IllegalArgumentException("samples must be >= 1, was " + samples);
        }
        this.lerper = lerper;
        this.duration = duration;
        this.samples = samples;
    }

    @Override
    public T apply(Tween<T> source, float t) {
        T r = source.value(t);
        for (int i = 1; i < samples; i++) {
            r = lerper.lerp(r, source.value(t - duration * i / samples), 1f / (i + 1));
        }
        return r;
    }
}
