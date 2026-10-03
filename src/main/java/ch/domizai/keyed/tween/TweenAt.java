package ch.domizai.keyed.tween;

/** A Tween with an optional fixed position at which a key samples it. */
public final class TweenAt<T> {
    private final Tween<T> tween;
    private final Float position;

    /** No fixed position: sampled at 0 leaving the key and at 1 arriving at it. */
    public TweenAt(Tween<T> tween) {
        this(tween, null);
    }

    /** Sampled at position at this key. */
    public TweenAt(Tween<T> tween, float position) {
        this(tween, Float.valueOf(position));
    }

    private TweenAt(Tween<T> tween, Float position) {
        this.tween = tween;
        this.position = position;
    }

    /** The wrapped tween. */
    public Tween<T> tween() {
        return tween;
    }

    /** The fixed position, or fallback if none. */
    public float positionOr(float fallback) {
        return position != null ? position : fallback;
    }
}