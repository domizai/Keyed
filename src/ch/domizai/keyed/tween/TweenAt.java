package ch.domizai.keyed.tween;

public final class TweenAt<T> {
    private final Tween<T> tween;
    private final Float position;

    public TweenAt(Tween<T> tween) {
        this(tween, null);
    }

    public TweenAt(Tween<T> tween, float position) {
        this(tween, Float.valueOf(position));
    }

    private TweenAt(Tween<T> tween, Float position) {
        this.tween = tween;
        this.position = position;
    }

    public Tween<T> tween() {
        return tween;
    }

    public float positionOr(float fallback) {
        return position != null ? position : fallback;
    }
}