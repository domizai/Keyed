package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.List;

// Public only because Processing invokes pre() reflectively; not meant to be used directly.
public final class Binder {
    private final List<KeyedBase<?, ?>> bound = new ArrayList<>();

    Binder() {}

    void add(KeyedBase<?, ?> keyed) {
        if (!bound.contains(keyed)) bound.add(keyed);
    }

    void remove(KeyedBase<?, ?> keyed) {
        bound.remove(keyed);
    }

    public void pre() {
        // Copy so setters may bind or unbind while applying.
        for (KeyedBase<?, ?> k : new ArrayList<>(bound)) {
            k.apply();
        }
    }
}
