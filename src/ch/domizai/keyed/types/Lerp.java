package ch.domizai.keyed.types;

@FunctionalInterface
public interface Lerp<T> {
    T lerp(T a, T b, float d);
}
