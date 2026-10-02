package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Key implements Comparable<Key> {
    private Pin frame;
    // Handle influence in [0, 1] as a fraction of the segment, with zero speed at the key; 0 is linear, 1/3 is AE's Easy Ease.
    private float easingIn = 0;
    private float easingOut = 0;
    // Replaces the influence curve on the segment leaving this key when set.
    private Easing easing;
    private ArrayList<Consumer<Pin>> listeners;

    public Key(Pin frame) {
        this.frame = frame;
    }

    public Key(float t) {
        this(new Pin(t));
    }

    public static Key at(Pin frame) {
        return new Key(frame); 
    }
    
    public static Key at(float t) {
        return new Key(t);
    }

    public Key to(float t) {
        frame.to(t);
        notifyListeners();
        return this;
    }

    public Pin getFrame() {
        return frame;
    }
    
    public Key setEasing(float influence) {
        return setEasingIn(influence).setEasingOut(influence);
    }

    // Curve for the whole segment from this key to the next, e.g. Easing.QUAD_OUT; ignores the next key's easingIn.
    public Key setEasing(Easing easing) {
        this.easing = easing;
        return this;
    }

    // Keeps this key's value until the next key, for stepped or stop-motion animation.
    public Key hold() {
        return setEasing(Easing.HOLD);
    }

    // Shapes the segment arriving at this key.
    public Key setEasingIn(float influence) {
        easingIn = checkInfluence(influence);
        return this;
    }

    // Shapes the segment leaving this key; clears an Easing set with setEasing(Easing).
    public Key setEasingOut(float influence) {
        easingOut = checkInfluence(influence);
        easing = null;
        return this;
    }
    
    public float t() {
        return frame.t();
    }

    public Key addListener(Consumer<Pin> listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
        return this;
    }

    public float easingIn() {
        return easingIn;
    }

    public float easingOut() {
        return easingOut;
    }

    // null when the segment uses the easingOut/easingIn influence curve.
    public Easing easing() {
        return easing;
    }

    private static float checkInfluence(float influence) {
        if (influence < 0 || influence > 1) {
            throw new IllegalArgumentException("easing must be in [0, 1], was " + influence);
        }
        return influence;
    }

    private void notifyListeners() {
        if (listeners == null) return;
        for (Consumer<Pin> listener : listeners) {
            listener.accept(frame);
        }
    }

    @Override
    public int compareTo(Key o) {
        return frame.compareTo(o.getFrame());
    }
}
