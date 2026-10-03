package ch.domizai.keyed.lerps;

@FunctionalInterface
public interface Lerp<T> {
    /** Blend from a (d = 0) to b (d = 1). */
    T lerp(T a, T b, float d);
}
