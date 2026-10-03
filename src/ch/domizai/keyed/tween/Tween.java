package ch.domizai.keyed.tween;

import java.util.function.Function;

@FunctionalInterface
public interface Tween<T> {
    /** Value at position d, normally 0..1. */
    T value(float d);

    /** Wraps this tween so a key samples it at a fixed position. */
    default TweenAt<T> at(float position) {
        return new TweenAt<>(this, position);
    }

    /** Applies f to each value. */
    default <R> Tween<R> map(Function<? super T, ? extends R> f) {
        return d -> f.apply(value(d));
    }
}
