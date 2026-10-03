package ch.domizai.keyed;

import java.util.ArrayList;
import java.util.List;

import ch.domizai.keyed.lerps.Lerp;

// Reusable animation with its own non-looping timeline; key times are local, 0 is when the instance starts.
// Extend it, create Keyed values with keyed() or add(), and dispose() once isFinished().
public class Composition {
    private final Timeline timeline;
    private final List<Keyed<?>> keyeds = new ArrayList<>();

    /** Composition lasting duration, in the timeline's unit. */
    public Composition(float duration) {
        timeline = new Timeline().setDuration(duration, false);
    }

    /** Creates a Keyed value on this composition's timeline. */
    protected <A> Keyed<A> keyed(Lerp<A> lerper, A defaultValue) {
        return add(new Keyed<>(lerper, defaultValue));
    }

    /** Moves the keyed value onto this composition's timeline and unbinds it on dispose(). */
    protected <A> Keyed<A> add(Keyed<A> keyed) {
        keyed.setTimeline(timeline);
        keyeds.add(keyed);
        return keyed;
    }

    /** This composition's own timeline. */
    public Timeline timeline() {
        return timeline;
    }

    /** Restarts from the beginning. */
    public Composition play() {
        timeline.to(0).play(true);
        return this;
    }

    /** Whether playback reached the end. */
    public boolean isFinished() {
        return timeline.isFinished();
    }

    /** Disposes the timeline and unbinds all keyed values. */
    public void dispose() {
        timeline.dispose();
        for (Keyed<?> k : keyeds) {
            k.unbind();
        }
        keyeds.clear();
    }
}
