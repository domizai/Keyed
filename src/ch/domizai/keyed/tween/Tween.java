package ch.domizai.keyed.tween;

public interface Tween<T> {
    Tween<T> clone();
    void set(T value);
    T value(float d);

    default TweenAt<T> at(float position) {
        return new TweenAt<>(this, position);
    }
}
