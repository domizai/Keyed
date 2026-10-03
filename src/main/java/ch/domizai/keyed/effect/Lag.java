package ch.domizai.keyed.effect;

import java.util.ArrayList;
import java.util.List;

import ch.domizai.keyed.lerps.Lerp;
import ch.domizai.keyed.tween.Tween;

/** Averages the value over the last {@code duration} seconds: trails behind and smooths out sharp moves, without overshoot. */
public class Lag<T> implements TimeEffect<T> {
    private final Lerp<T> lerper;
    private final float duration;
    private final int samples;

    /** duration in seconds; more samples is smoother but costs one source evaluation each. */
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

    // Samples sit on a fixed grid of times and the output blends the averages at the grid points
    // before and after t, so it moves smoothly even when the source jumps (e.g. hold keys).
    @Override
    public T apply(Tween<T> source, float t) {
        double step = (double) duration / samples;
        double g = Math.floor(t / step);
        float f = (float) (t / step - g);
        List<T> values = new ArrayList<>(samples + 1);
        for (int k = 0; k <= samples; k++) {
            values.add(source.value((float) ((g - k) * step)));
        }
        return lerper.lerp(average(values, 1), average(values, 0), f);
    }

    // Average of samples values, starting offset grid steps back.
    private T average(List<T> values, int offset) {
        T r = values.get(offset);
        for (int i = 1; i < samples; i++) {
            r = lerper.lerp(r, values.get(i + offset), 1f / (i + 1));
        }
        return r;
    }
}
