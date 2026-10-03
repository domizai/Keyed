package ch.domizai.keyed;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static processing.core.PApplet.constrain;

/**
 * Playback clock for Keyed values and markers, in its Unit (seconds by default).
 * Created with a sketch it advances by itself before every draw(); otherwise call step().
 */
public class Timeline {
    // Caps callbacks when one jump spans many loops.
    private static final int MAX_WRAPS = 100;
    private static final Comparator<Marker> BY_TIME = Comparator.comparingDouble(Marker::t);

    private Unit unit = Keyed.unit();
    private boolean synced = Keyed.isSynced();
    private boolean autoplay = Keyed.isAutoplay();
    private boolean loop = true;
    private float duration = 0;
    private float t = 0;
    private float speed = 1;
    private float fixedStep = 0;
    private boolean playing = true;
    private long lastNanos = -1;
    private boolean started = false;
    private PApplet sketch;
    private final List<Marker> markers = new ArrayList<>();
    private final List<Consumer<Timeline>> loopListeners = new ArrayList<>();
    private final List<Consumer<Timeline>> finishListeners = new ArrayList<>();
    // Set by moves that fire nothing, so a marker exactly at the new t still fires on the next step.
    private boolean arrivedSilently = true;
    // Bumped on every move; a change during a callback means it moved the timeline, so the rest is skipped.
    private int moveId = 0;

    /** Follows real time if Keyed.init() was called; otherwise only moves via step() or to(). */
    public Timeline() {
        this(Keyed.sketch());
    }

    /** Advances before every draw() of sketch; null only moves via step() or to(). */
    public Timeline(PApplet sketch) {
        this.sketch = sketch;
        if (sketch != null) {
            sketch.registerMethod("pre", this);
        }
    }

    /** Called by Processing before each draw(); public only so it can be registered. The first frame shows t = 0. */
    public void pre() {
        if (!autoplay) {
            // Re-enabling autoplay starts a fresh clock instead of jumping by the paused gap.
            started = false;
            return;
        }
        if (started) {
            step();
        } else {
            started = true;
            lastNanos = System.nanoTime();
        }
    }

    /** Advances by the fixed step if set; otherwise by real time when synced, or by one frame when not; times speed. */
    public Timeline step() {
        long now = System.nanoTime();
        float fps = Keyed.frameRate();
        float dt;
        boolean exact = fixedStep > 0 || !synced;
        if (fixedStep > 0) {
            dt = fixedStep;
        } else if (synced) {
            float seconds = lastNanos < 0 ? 0 : (now - lastNanos) / 1e9f;
            dt = unit == Unit.FRAME ? seconds * fps : seconds;
        } else {
            dt = unit == Unit.FRAME ? 1 : 1 / fps;
        }
        lastNanos = now;
        return exact ? stepExact(dt * speed) : step(dt * speed);
    }

    // Adding a step like 1/30 again and again drifts in float (90 steps give 2.9999943, not 3), so a loop
    // would wrap one step late; snapping back onto the step grid and the loop boundaries keeps t exact.
    private Timeline stepExact(float amount) {
        if (!playing || amount == 0) {
            return step(amount);
        }
        double grid = Math.abs((double) amount);
        double target = (double) t + amount;
        double tolerance = grid * 1e-3;
        long k = Math.round(target / grid);
        if (Math.abs(target - k * grid) < tolerance) {
            target = k * grid;
        }
        if (duration > 0) {
            long m = Math.round(target / duration);
            if (Math.abs(target - (double) m * duration) < tolerance) {
                target = (double) m * duration;
            }
        }
        move((float) target, true);
        return this;
    }

    /** Advances by amount while playing; fires markers, onLoop and onFinish callbacks passed on the way. */
    public Timeline step(float amount) {
        if (!playing)
            return this;
        move(t + amount, true);
        return this;
    }
    
    /** Whether the timeline is playing; see play(). */
    public boolean isPlaying() {
        return playing;
    }

    /** Resumes or pauses; while paused step() does nothing. */
    public Timeline play(boolean p) {
        if (p && !playing) {
            lastNanos = -1;
        }
        playing = p;
        return this;
    }

    /** Current time. */
    public float t() {
        return t;
    }

    /** Time at offset from now (negative = past), wrapped or clamped like the timeline itself. */
    public float t(float offset) {
        return fit(t + offset);
    }

    /** Unit of t, duration and step amounts. */
    public Unit unit() {
        return unit;
    }

    /** Only changes how step() advances; t, keys and duration are not converted. */
    public Timeline setUnit(Unit unit) {
        this.unit = unit;
        return this;
    }

    /** Whether step() follows the real clock; see sync(). */
    public boolean isSynced() {
        return synced;
    }

    /** true follows the real clock; false advances exactly one frame (Keyed.frameRate()) per step(), for deterministic exports. */
    public Timeline sync(boolean sync) {
        this.synced = sync;
        return this;
    }

    /** Whether step() runs automatically before each draw(); see autoplay(). */
    public boolean isAutoplay() {
        return autoplay;
    }

    /** false stops the automatic step() before each draw(), so step() can be called manually; sync still applies. */
    public Timeline autoplay(boolean autoplay) {
        this.autoplay = autoplay;
        return this;
    }

    /** Duration; 0 means unbounded. */
    public float duration() {
        return duration;
    }

    /** Multiplier for step(); see setSpeed(). */
    public float speed() {
        return speed;
    }

    /** Multiplier for step(); negative plays backwards. */
    public Timeline setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    /** Units per step() set with setFixedStep(); 0 when disabled. */
    public float fixedStep() {
        return fixedStep;
    }

    /** Units per step() regardless of real time, e.g. 1f / 30 seconds for frame-exact saveFrame() exports; 0 disables it. */
    public Timeline setFixedStep(float amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("fixed step must be >= 0, was " + amount);
        }
        this.fixedStep = amount;
        return this;
    }

    /** Jumps without firing markers or callbacks. */
    public Timeline to(float t) {
        return to(t, false);
    }

    /** Jumps to t; fire = true treats the jump like playback, e.g. when scrubbing with the mouse. */
    public Timeline to(float t, boolean fire) {
        move(t, fire);
        return this;
    }

    /** Calls callback whenever playback crosses pin. */
    public Marker addMarker(Pin pin, Consumer<Marker> callback) {
        return addMarker(null, pin, callback);
    }

    /** Calls callback whenever playback crosses t. */
    public Marker addMarker(float t, Consumer<Marker> callback) {
        return addMarker(null, Pin.at(t), callback);
    }

    /** Named marker at t; name may be null. */
    public Marker addMarker(String name, float t, Consumer<Marker> callback) {
        return addMarker(name, Pin.at(t), callback);
    }

    /** Named marker on pin; name may be null. */
    public Marker addMarker(String name, Pin pin, Consumer<Marker> callback) {
        Marker m = new Marker(this, name, pin, callback);
        markers.add(m);
        return m;
    }

    /** Removes a marker; safe to call from a marker callback. */
    public Timeline removeMarker(Marker marker) {
        markers.remove(marker);
        return this;
    }

    /** First marker with this name, or null. */
    public Marker marker(String name) {
        for (Marker m : markers) {
            if (name.equals(m.name())) return m;
        }
        return null;
    }

    /** Markers sorted by time. */
    public List<Marker> markers() {
        List<Marker> list = new ArrayList<>(markers);
        list.sort(BY_TIME);
        return list;
    }

    /** Called each time a looping timeline wraps around, in either direction. */
    public Timeline onLoop(Consumer<Timeline> callback) {
        loopListeners.add(callback);
        return this;
    }

    /** Called once when a non-looping timeline reaches its duration. */
    public Timeline onFinish(Consumer<Timeline> callback) {
        finishListeners.add(callback);
        return this;
    }

    /** Removes a callback added with onLoop() or onFinish(). */
    public Timeline removeListener(Consumer<Timeline> callback) {
        loopListeners.remove(callback);
        finishListeners.remove(callback);
        return this;
    }

    /** Sets the duration and enables looping. */
    public Timeline setDuration(float duration) {
        return setDuration(duration, true);
    }

    /** Sets the duration (0 = unbounded) and looping; t is wrapped or clamped to fit. */
    public Timeline setDuration(float duration, boolean loop) {
        this.duration = duration;
        this.loop = loop;
        t = fit(t);
        return this;
    }

    /** Whether the timeline wraps at its duration; see loop(). */
    public boolean isLooping() {
        return loop;
    }

    /** true wraps at the duration; false stops there. */
    public Timeline loop(boolean l) {
        loop = l;
        return this;
    }

    /** True once a non-looping timeline has reached its duration. */
    public boolean isFinished() {
        return !loop && duration > 0 && t >= duration;
    }

    /** Stops advancing with the sketch; call when the timeline is no longer needed so it can be garbage collected. */
    public void dispose() {
        if (sketch != null) {
            sketch.unregisterMethod("pre", this);
            sketch = null;
        }
    }

    private float fit(float t) {
        if (duration <= 0)
            return t;
        return loop ? remEuclid(t, duration) : constrain(t, 0, duration);
    }

    private void move(float target, boolean fire) {
        int id = ++moveId;
        float from = t;
        boolean wasFinished = isFinished();
        boolean includeStart = arrivedSilently;
        t = fit(target);
        arrivedSilently = !fire;
        if (!fire) return;

        if (!crossMarkers(from, target - from, includeStart, id)) return;
        if (!wasFinished && isFinished()) {
            callListeners(finishListeners, id);
        }
    }

    // Walks from `from` by delta, wrapping like fit(); false if a callback moved the timeline.
    private boolean crossMarkers(float from, float delta, boolean includeStart, int id) {
        boolean wraps = loop && duration > 0;
        boolean forward = delta >= 0;
        float pos = from;
        float remaining = Math.abs(delta);
        boolean inclusive = includeStart;
        for (int i = 0; i <= MAX_WRAPS; i++) {
            if (forward) {
                float end = pos + remaining;
                if (!wraps || end < duration) {
                    return fireMarkers(pos, wraps ? end : t, inclusive, true, true, id);
                }
                if (!fireMarkers(pos, duration, inclusive, true, true, id)) return false;
                remaining = end - duration;
                pos = 0;
            } else {
                float end = pos - remaining;
                if (!wraps || end >= 0) {
                    return fireMarkers(wraps ? end : t, pos, true, inclusive, false, id);
                }
                if (!fireMarkers(0, pos, true, inclusive, false, id)) return false;
                remaining = -end;
                pos = duration;
            }
            inclusive = true;
            if (!callListeners(loopListeners, id)) return false;
        }
        return true;
    }

    // Fires markers in [lo, hi] (ends included as given) in playback order.
    private boolean fireMarkers(float lo, float hi, boolean includeLo, boolean includeHi, boolean forward, int id) {
        List<Marker> crossed = new ArrayList<>();
        for (Marker m : markers) {
            float mt = m.t();
            if ((mt > lo || includeLo && mt == lo) && (mt < hi || includeHi && mt == hi)) {
                crossed.add(m);
            }
        }
        crossed.sort(forward ? BY_TIME : BY_TIME.reversed());
        for (Marker m : crossed) {
            // Skips markers removed by an earlier callback in this step.
            if (!markers.contains(m)) continue;
            m.fire();
            if (moveId != id) return false;
        }
        return true;
    }

    private boolean callListeners(List<Consumer<Timeline>> listeners, int id) {
        for (Consumer<Timeline> l : new ArrayList<>(listeners)) {
            l.accept(this);
            if (moveId != id) return false;
        }
        return true;
    }

    private float remEuclid(float a, float b) {
        return (a % b + b) % b;
    }
}
