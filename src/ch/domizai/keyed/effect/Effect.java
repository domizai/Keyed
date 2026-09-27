package ch.domizai.keyed.effect;

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
}
