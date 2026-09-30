package ch.domizai.keyed;

import processing.core.PApplet;

import static processing.core.PApplet.constrain;

// Time is in the timeline's Unit (seconds by default). Created with a sketch it advances by itself before every draw(); otherwise call step().
public class Timeline {
    private Unit unit = Keyed.unit();
    private boolean synced = Keyed.isSynced();
    private boolean loop = true;
    private float duration = 0;
    private float t = 0;
    private float speed = 1;
    private float fixedStep = 0;
    private boolean playing = true;
    private long lastNanos = -1;
    private boolean started = false;

    // Follows real time if Keyed.init() was called; otherwise only moves via step() or to().
    public Timeline() {
        PApplet sketch = Keyed.sketch();
        if (sketch != null) {
            sketch.registerMethod("pre", this);
        }
    }

    public Timeline(PApplet sketch) {
        sketch.registerMethod("pre", this); // this will call the pre() method before each draw() in the sketch
    }

    // Called by Processing before each draw(); public only so it can be registered. The first frame shows t = 0.
    public void pre() {
        if (started) {
            step();
        } else {
            started = true;
            lastNanos = System.nanoTime();
        }
    }

    // Advances by the fixed step if set; otherwise by real time when synced, or by one frame when not; times speed.
    public Timeline step() {
        long now = System.nanoTime();
        float fps = Keyed.frameRate();
        float dt;
        if (fixedStep > 0) {
            dt = fixedStep;
        } else if (synced) {
            float seconds = lastNanos < 0 ? 0 : (now - lastNanos) / 1e9f;
            dt = unit == Unit.FRAME ? seconds * fps : seconds;
        } else {
            dt = unit == Unit.FRAME ? 1 : 1 / fps;
        }
        lastNanos = now;
        return step(dt * speed);
    }

    public Timeline step(float amount) {
        if (!playing)
            return this;
        t = fit(t + amount);
        return this;
    }
    
    public Timeline play(boolean p) {
        if (p && !playing) {
            lastNanos = -1;
        }
        playing = p;
        return this;
    }

    public float t() {
        return t;
    }

    // Offset from now (negative = past), wrapped or clamped like the timeline itself.
    public float t(float offset) {
        return fit(t + offset);
    }

    public Unit unit() {
        return unit;
    }

    // Only changes how step() advances; t, keys and duration are not converted.
    public Timeline setUnit(Unit unit) {
        this.unit = unit;
        return this;
    }

    public boolean isSynced() {
        return synced;
    }

    // true follows the real clock; false advances exactly one frame (Keyed.frameRate()) per step(), for deterministic exports.
    public Timeline sync(boolean sync) {
        this.synced = sync;
        return this;
    }

    public float duration() {
        return duration;
    }

    public Timeline setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    // Units per step() regardless of real time, e.g. 1f / 30 seconds for frame-exact saveFrame() exports; 0 uses the unit's default.
    public Timeline setFixedStep(float amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("fixed step must be >= 0, was " + amount);
        }
        this.fixedStep = amount;
        return this;
    }

    public Timeline to(float t) {
        this.t = fit(t);
        return this;
    }

    public Timeline setDuration(float duration) {
        return setDuration(duration, true);
    }

    public Timeline setDuration(float duration, boolean loop) {
        this.duration = duration;
        this.loop = loop;
        t = fit(t);
        return this;
    }

    public Timeline loop(boolean l) {
        loop = l;
        return this;
    }

    private float fit(float t) {
        if (duration <= 0)
            return t;
        return loop ? remEuclid(t, duration) : constrain(t, 0, duration);
    }

    private float remEuclid(float a, float b) {
        return (a % b + b) % b;
    }
}
