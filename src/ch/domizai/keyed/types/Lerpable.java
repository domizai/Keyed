package ch.domizai.keyed.types;

// A type that blends itself with another value; use with Keyed.of(value) instead of passing a separate Lerp.
public interface Lerpable<T extends Lerpable<T>> {
    /** Blends toward b by d in [0, 1]; must return a new object for mutable types, so stored keys can't be changed through the result. */
    T lerp(T b, float d);
}
