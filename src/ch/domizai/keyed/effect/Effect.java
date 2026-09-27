package ch.domizai.keyed.effect;

import processing.core.PVector;

public interface Effect<T> {
    T apply(T value, float t);

    public static Effect<PVector> WIGGLE(float amplitude, float frequency) {
        return new Wiggle(frequency, amplitude);
    }
}
