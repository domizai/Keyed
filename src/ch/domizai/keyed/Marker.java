package ch.domizai.keyed;

import java.util.function.Consumer;

// A point on a Timeline that calls back when playback crosses it; create with Timeline.addMarker().
public class Marker {
    private final Timeline timeline;
    private final String name;
    private final Pin pin;
    private final Consumer<Marker> callback;

    Marker(Timeline timeline, String name, Pin pin, Consumer<Marker> callback) {
        this.timeline = timeline;
        this.name = name;
        this.pin = pin;
        this.callback = callback;
    }

    // null if unnamed.
    public String name() {
        return name;
    }

    // Follows the pin, so moving the pin moves the marker.
    public float t() {
        return pin.t();
    }

    public Pin pin() {
        return pin;
    }

    public Timeline timeline() {
        return timeline;
    }

    void fire() {
        callback.accept(this);
    }
}
