package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.List;

/** Public only because Processing invokes pre() reflectively; not meant to be used directly. */
public final class Binder {
    private final List<Keyed<?>> bound = new ArrayList<>();
    private final List<Timeline> timelines = new ArrayList<>();

    Binder() {}

    void add(Keyed<?> keyed) {
        if (!bound.contains(keyed)) bound.add(keyed);
    }

    void remove(Keyed<?> keyed) {
        bound.remove(keyed);
    }

    void addTimeline(Timeline timeline) {
        if (!timelines.contains(timeline)) timelines.add(timeline);
    }

    void removeTimeline(Timeline timeline) {
        timelines.remove(timeline);
    }

    /** Advances all timelines, then applies all bound values, so bound values match value() in draw(). */
    public void pre() {
        for (Timeline t : new ArrayList<>(timelines)) {
            t.pre();
        }
        // Copy so setters may bind or unbind while applying.
        for (Keyed<?> k : new ArrayList<>(bound)) {
            k.apply();
        }
    }
}
