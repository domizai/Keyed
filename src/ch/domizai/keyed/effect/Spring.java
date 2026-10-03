package ch.domizai.keyed.effect;

import ch.domizai.keyed.lerps.Lerp;
import ch.domizai.keyed.tween.Tween;

// Damped spring following the value: lags while it moves, overshoots and settles when it stops.
// Needs a Lerp that extrapolates beyond [0, 1] (PVectorLerp, FloatLerp); ColorLerp clamps, so no overshoot.
public class Spring<T> implements TimeEffect<T> {
    private static final int SAMPLES_PER_PERIOD = 16;
    // The response is cut off once the oscillation envelope falls below this.
    private static final double CUTOFF = 1e-3;

    private final Lerp<T> lerper;
    private final float[] delays, weights;

    // frequency: oscillations per second; keep it below half the frame rate or the wobble can't be seen.
    // damping: ratio in (0, 1); lower wobbles longer and costs more samples.
    public Spring(Lerp<T> lerper, float frequency, float damping) {
        if (frequency <= 0) {
            throw new IllegalArgumentException("frequency must be > 0, was " + frequency);
        }
        if (damping <= 0 || damping >= 1) {
            throw new IllegalArgumentException("damping must be in (0, 1), was " + damping);
        }
        this.lerper = lerper;

        double w = 2 * Math.PI * frequency;
        double wd = w * Math.sqrt(1 - damping * damping);
        double dt = 2 * Math.PI / wd / SAMPLES_PER_PERIOD;
        int n = (int) Math.ceil(Math.log(1 / CUTOFF) / (damping * w) / dt);

        // Impulse response of y'' + 2*damping*w*y' + w^2*y = w^2*x, sampled at interval midpoints.
        double[] h = new double[n];
        double sum = 0;
        for (int i = 0; i < n; i++) {
            double tau = (i + 0.5) * dt;
            h[i] = Math.exp(-damping * w * tau) * Math.sin(wd * tau);
            sum += h[i];
        }
        delays = new float[n];
        weights = new float[n];
        for (int i = 0; i < n; i++) {
            delays[i] = (float) ((i + 0.5) * dt);
            weights[i] = (float) (h[i] / sum);
        }
    }

    @Override
    public T apply(Tween<T> source, float t) {
        T r = source.value(t - delays[0]);
        float total = weights[0];
        for (int i = 1; i < delays.length; i++) {
            total += weights[i];
            r = lerper.lerp(r, source.value(t - delays[i]), weights[i] / total);
        }
        return r;
    }
}
