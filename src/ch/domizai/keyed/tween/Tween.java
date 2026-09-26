package ch.domizai.keyed.tween;

@FunctionalInterface
public interface Tween<T> {
    T lerp(T a, T b, float d);
}
