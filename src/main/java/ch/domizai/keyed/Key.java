package ch.domizai.keyed;

/** A keyframe time, with easing for the segments around it. */
public class Key implements Comparable<Key> {
    private Pin pin;
    // Handle influence in [0, 1] as a fraction of the segment, with zero speed at the key; 0 is linear, 1/3 is AE's Easy Ease.
    private float easingIn = 0;
    private float easingOut = 0;
    // Replaces the influence curve on the segment leaving this key when set.
    private Easing easing;

    /** Key at a shared pin. */
    public Key(Pin pin) {
        this.pin = pin;
    }

    /** Key at time t with its own pin. */
    public Key(float t) {
        this(new Pin(t));
    }

    /** Key at a shared pin. */
    public static Key at(Pin pin) {
        return new Key(pin); 
    }
    
    /** Key at time t with its own pin. */
    public static Key at(float t) {
        return new Key(t);
    }

    /** Moves the pin, so every key sharing it moves too. */
    public Key to(float t) {
        pin.to(t);
        return this;
    }

    /** Pin holding this key's time. */
    public Pin pin() {
        return pin;
    }
    
    /** Sets both easingIn and easingOut influence, in [0, 1]. */
    public Key setEasing(float influence) {
        return setEasingIn(influence).setEasingOut(influence);
    }

    /** Curve for the whole segment from this key to the next, e.g. Easing.QUAD_OUT; ignores the next key's easingIn. */
    public Key setEasing(Easing easing) {
        this.easing = easing;
        return this;
    }

    /** Keeps this key's value until the next key, for stepped or stop-motion animation. */
    public Key hold() {
        return setEasing(Easing.HOLD);
    }

    /** Shapes the segment arriving at this key; influence in [0, 1]. */
    public Key setEasingIn(float influence) {
        easingIn = checkInfluence(influence);
        return this;
    }

    /** Shapes the segment leaving this key; clears an Easing set with setEasing(Easing). */
    public Key setEasingOut(float influence) {
        easingOut = checkInfluence(influence);
        easing = null;
        return this;
    }
    
    /** Time of this key's pin. */
    public float t() {
        return pin.t();
    }

    /** Influence of the arriving segment, in [0, 1]. */
    public float easingIn() {
        return easingIn;
    }

    /** Influence of the leaving segment, in [0, 1]. */
    public float easingOut() {
        return easingOut;
    }

    /** Curve of the leaving segment; null when it uses the easingOut/easingIn influence curve. */
    public Easing easing() {
        return easing;
    }

    private static float checkInfluence(float influence) {
        if (influence < 0 || influence > 1) {
            throw new IllegalArgumentException("easing must be in [0, 1], was " + influence);
        }
        return influence;
    }

    @Override
    public int compareTo(Key o) {
        return pin.compareTo(o.pin);
    }
}
