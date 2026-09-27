package ch.domizai.keyed.lerps;

@FunctionalInterface
public interface Lerp<T> {
    T lerp(T a, T b, float d);
}
