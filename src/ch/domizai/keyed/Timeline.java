package ch.domizai.keyed;

import processing.core.PApplet;

public class Timeline extends PApplet {
    private boolean loop = true;
    private float duration = 0;
    private float t = 0;
    private float delta = 1;
    private boolean playing = true;

    public Timeline step(float delta) {
        if (!playing)
            return this;
        t += delta;
        if (duration > 0)
            t = loop ? remEuclid(t, duration) : constrain(t, 0, duration - delta);
        return this;
    }

    public Timeline step() {
        return step(delta);
    }
    
    public Timeline play(boolean p) {
        playing = p;
        return this;
    }

    public float t() {
        return t;
    }

    public float duration() {
        return duration;
    }

    public Timeline setDelta(float delta) {
        this.delta = delta;
        return this;
    }

    public Timeline to(float t) {
        this.t = duration > 0 ? remEuclid(t, duration) : t;
        return this;
    }

    public Timeline setDuration(float duration) {
        return setDuration(duration, true);
    }

    public Timeline setDuration(float duration, boolean loop) {
        this.duration = duration;
        this.loop = loop;
        t = remEuclid(t, duration);
        return this;
    }

    public Timeline loop(boolean l) {
        loop = l;
        return this;
    }

    private float remEuclid(float a, float b) {
        return (a % b + b) % b;
    }
}
