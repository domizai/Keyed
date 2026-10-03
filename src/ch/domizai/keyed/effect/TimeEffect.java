package ch.domizai.keyed.effect;

import ch.domizai.keyed.tween.Tween;

// For effects that need the animation at other times; source gives the value before this effect.
@FunctionalInterface
public interface TimeEffect<T> {
    /** Value at timeline time t, sampling source as needed. */
    T apply(Tween<T> source, float t);
}
