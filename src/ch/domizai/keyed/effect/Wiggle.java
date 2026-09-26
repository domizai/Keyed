package ch.domizai.keyed.effect;

import processing.core.PApplet;
import processing.core.PVector;

public class Wiggle extends PApplet implements Effect<PVector> {
    private PVector off, y;
    private float frequency, amplitude;

    public Wiggle(float frequency, float amplitude) {
        long seed = (long)(amplitude * frequency * 7919);
        randomSeed(seed);
        noiseSeed(seed);
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.off = new PVector(random(65536), random(65536));
        this.y = new PVector(random(65536), random(65536));
    }

    @Override
    public PVector apply(PVector p, float t) {
        off.set(off.x + frequency, off.y + frequency);
        return new PVector(
            p.x + (noise(off.x, y.x) - 0.5f) * amplitude,
            p.y + (noise(off.y, y.y) - 0.5f) * amplitude);
    }
}
