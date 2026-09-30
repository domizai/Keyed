package ch.domizai.keyed.tween;

import java.util.function.Function;

@FunctionalInterface
public interface Tween<T> {
    T value(float d);

    default TweenAt<T> at(float position) {
        return new TweenAt<>(this, position);
    }

    default <R> Tween<R> map(Function<? super T, ? extends R> f) {
        return d -> f.apply(value(d));
    }
}
