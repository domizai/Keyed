package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// A point in time that several keys can share; moving it moves all of them.
public class Pin implements Comparable<Pin> {
    private float t;
    private List<Consumer<Pin>> listeners;

    /** Pin at time 0. */
    public Pin() {
    }

    /** Pin at time t. */
    public Pin(float t) {
        this.t = t;
    }

    /** Pin at time t. */
    public static Pin at(float t) {
        return new Pin(t);
    }

    /** Current time. */
    public float t() {
        return t;
    }

    /** Moves to time t and notifies listeners. */
    public Pin to(float t) {
        this.t = t;
        if (listeners != null) {
            // Copy so listeners may remove themselves.
            for (Consumer<Pin> listener : new ArrayList<>(listeners)) {
                listener.accept(this);
            }
        }
        return this;
    }

    /** Adds a listener called after every to(), including moves made through a Key. */
    public Pin addListener(Consumer<Pin> listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
        return this;
    }

    /** Removes a listener added with addListener(). */
    public Pin removeListener(Consumer<Pin> listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
        return this;
    }

    @Override
    public int compareTo(Pin o) {
        return Float.compare(this.t, o.t);
    }
}
