package ch.domizai.keyed.effect;

import ch.domizai.keyed.lerp.PVectorLerp;
import ch.domizai.keyed.tween.Tween;
import processing.core.PVector;

public interface Effect<T> extends TimeEffect<T> {
    T apply(T value, float t);

    @Override
    default T apply(Tween<T> source, float t) {
        return apply(source.value(t), t);
    }

    public static Effect<PVector> WIGGLE(float amplitude, float frequency) {
        return new Wiggle(frequency, amplitude);
    }

    public static Effect<PVector> ORBIT(float radius, float frequency) {
        return new Orbit(radius, frequency);
    }

    public static Effect<PVector> PIXEL_SNAP(float gridSize) {
        return new PixelSnap(gridSize);
    }

    public static TimeEffect<PVector> SPRING(float stiffness, float damping) {
        return new Spring<>(new PVectorLerp(), stiffness, damping);
    }

    public static TimeEffect<PVector> LAG(float duration, int samples) {
        return new Lag<>(new PVectorLerp(), duration, samples);
    }
}
