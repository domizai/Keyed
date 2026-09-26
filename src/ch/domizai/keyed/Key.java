package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.function.Consumer;

import ch.domizai.keyed.easing.Easing;
import ch.domizai.keyed.easing.LinearEasing;


public class Key implements Comparable<Key> {
    private Frame frame;
    private Easing easeIn;
    private Easing easeOut;
    private ArrayList<Consumer<Frame>> listeners;

    public Key(Frame frame) {
        this.frame = frame;
        this.easeIn = new LinearEasing();
        this.easeOut = new LinearEasing();
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

    // public Key toRelative(float t) {
    //     frame.toRelative(t);
    //     fireListeners();
    //     return this;
    // }

    public Frame getFrame() {
        return frame;
    }
    
    public Key setEaseInOut(Easing easeInOut) {
        this.easeIn = easeInOut;
        this.easeOut = easeInOut;
        return this;
    }

    public Key setEaseIn(Easing easeIn) {
        this.easeIn = easeIn;
        return this;
    }
    
    public Key setEaseOut(Easing easeOut) {
        this.easeOut = easeOut;
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

    public Easing easeIn() {
        return easeIn;
    }

    public Easing easeOut() {
        return easeOut;
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
