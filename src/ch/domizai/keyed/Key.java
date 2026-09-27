package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Key implements Comparable<Key> {
    private Frame frame;
    // Shapes the segment arriving at this key; unused on the first key.
    private Easing easing = Easing.LINEAR;
    private ArrayList<Consumer<Frame>> listeners;

    public Key(Frame frame) {
        this.frame = frame;
    }

    public Key(float t) {
        this(new Frame(t));
    }

    public static Key at(Frame frame) {
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

    public Frame getFrame() {
        return frame;
    }
    
    public Key setEasing(Easing easing) {
        this.easing = easing;
        return this;
    }
    
    public float t() {
        return frame.t();
    }

    public Key addListener(Consumer<Frame> listener) {
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
        for (Consumer<Frame> listener : listeners) {
            listener.accept(frame);
        }
    }

    @Override
    public int compareTo(Key o) {
        return frame.compareTo(o.getFrame());
    }
}
