package ch.domizai.keyed.tween;

@FunctionalInterface
public interface Tween<T> {
    T value(float d);

    default TweenAt<T> at(float position) {
        return new TweenAt<>(this, position);
    }
}
