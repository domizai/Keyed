package ch.domizai.keyed;

import processing.core.PApplet;

import static processing.core.PApplet.constrain;

// Time is in seconds. Created with a sketch it advances by itself before every draw(); otherwise call step().
public class Timeline {
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

    // Advances by the real time since the previous call (or the fixed step, if set), times speed.
    public Timeline step() {
        long now = System.nanoTime();
        float dt = fixedStep > 0 ? fixedStep : lastNanos < 0 ? 0 : (now - lastNanos) / 1e9f;
        lastNanos = now;
        return step(dt * speed);
    }

    public Timeline step(float seconds) {
        if (!playing)
            return this;
        t = fit(t + seconds);
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

    // Time offset seconds from now (negative = past), wrapped or clamped like the timeline itself.
    public float t(float offset) {
        return fit(t + offset);
    }

    public float duration() {
        return duration;
    }

    public Timeline setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    // Seconds per step() regardless of real time, e.g. 1f / 30 for frame-exact saveFrame() exports; 0 uses real time.
    public Timeline setFixedStep(float seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("fixed step must be >= 0, was " + seconds);
        }
        this.fixedStep = seconds;
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
