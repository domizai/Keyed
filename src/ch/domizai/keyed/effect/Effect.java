package ch.domizai.keyed.effect;

import ch.domizai.keyed.tween.Tween;
import ch.domizai.keyed.types.PVectorLerp;
import processing.core.PVector;

public interface Effect<T> extends TimeEffect<T> {
    T apply(T value, float t);

    @Override
    default T apply(Tween<T> source, float t) {
        return apply(source.value(t), t);
    }

    // Shortcuts for PVector values; same argument order as the constructors.
    public static Effect<PVector> wiggle(float amplitude, float frequency) {
        return new Wiggle(amplitude, frequency);
    }

    public static Effect<PVector> orbit(float radius, float frequency) {
        return new Orbit(radius, frequency);
    }

    public static Effect<PVector> pixelSnap(float gridSize) {
        return new PixelSnap(gridSize);
    }

    public static TimeEffect<PVector> spring(float frequency, float damping) {
        return new Spring<>(new PVectorLerp(), frequency, damping);
    }

    public static TimeEffect<PVector> lag(float duration, int samples) {
        return new Lag<>(new PVectorLerp(), duration, samples);
    }
}
