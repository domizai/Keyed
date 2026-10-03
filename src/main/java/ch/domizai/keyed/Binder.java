package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.List;

/** Public only because Processing invokes pre() reflectively; not meant to be used directly. */
public final class Binder {
    private final List<Keyed<?>> bound = new ArrayList<>();

    Binder() {}

    void add(Keyed<?> keyed) {
        if (!bound.contains(keyed)) bound.add(keyed);
    }

    void remove(Keyed<?> keyed) {
        bound.remove(keyed);
    }

    /** Applies all bound values; called by Processing before each draw(). */
    public void pre() {
        // Copy so setters may bind or unbind while applying.
        for (Keyed<?> k : new ArrayList<>(bound)) {
            k.apply();
        }
    }
}
