package ch.domizai.keyed.lerp;

@FunctionalInterface
public interface Lerp<T> {
    T lerp(T a, T b, float d);
}
