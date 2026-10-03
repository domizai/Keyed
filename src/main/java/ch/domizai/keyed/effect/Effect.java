package ch.domizai.keyed.effect;

import ch.domizai.keyed.lerps.PVectorLerp;
import ch.domizai.keyed.tween.Tween;
import processing.core.PVector;

/** A TimeEffect that only needs the current value and time. */
public interface Effect<T> extends TimeEffect<T> {
    /** Modifies value at timeline time t. */
    T apply(T value, float t);

    @Override
    default T apply(Tween<T> source, float t) {
        return apply(source.value(t), t);
    }

    // Shortcuts for PVector values; same argument order as the constructors.

    /** Smooth random offset in x and y within ±amplitude; frequency in wiggles per second. */
    public static Effect<PVector> wiggle(float amplitude, float frequency) {
        return new Wiggle(amplitude, frequency);
    }

    /** Circles in the XY plane; frequency in cycles per second. */
    public static Effect<PVector> orbit(float radius, float frequency) {
        return new Orbit(radius, frequency);
    }

    /** Circles around axis; frequency in cycles per second. */
    public static Effect<PVector> orbit(float radius, float frequency, PVector axis) {
        return new Orbit(radius, frequency, axis);
    }

    /** Snaps x and y to a grid of gridSize. */
    public static Effect<PVector> pixelSnap(float gridSize) {
        return new PixelSnap(gridSize);
    }

    /** Damped spring; frequency in oscillations per second, damping in (0, 1). */
    public static TimeEffect<PVector> spring(float frequency, float damping) {
        return new Spring<>(new PVectorLerp(), frequency, damping);
    }

    /** Averages over the last duration seconds from samples evaluations. */
    public static TimeEffect<PVector> lag(float duration, int samples) {
        return new Lag<>(new PVectorLerp(), duration, samples);
    }
}
