package ch.domizai.keyed.lerps;

/** Blends two values of a type. */
@FunctionalInterface
public interface Lerp<T> {
    /** Blend from a (d = 0) to b (d = 1). */
    T lerp(T a, T b, float d);
}
