package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Key implements Comparable<Key> {
    private Pin frame;
    // Shapes the segment arriving at this key; unused on the first key.
    private Easing easing = Easing.LINEAR;
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
    
    public Key setEasing(Easing easing) {
        this.easing = easing;
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

    public Easing easing() {
        return easing;
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
