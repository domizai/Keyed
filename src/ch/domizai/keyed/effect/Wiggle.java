package ch.domizai.keyed.effect;

import processing.core.PApplet;
import processing.core.PVector;

public class Wiggle implements Effect<PVector> {
    // Private seeded source for noise() and random(); never run as a sketch.
    private final PApplet rng = new PApplet();
    private final PVector off, y;
    private final float frequency, amplitude;

    public Wiggle(float frequency, float amplitude) {
        long seed = (long)(amplitude * frequency * 7919);
        rng.randomSeed(seed);
        rng.noiseSeed(seed);
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.off = new PVector(rng.random(65536), rng.random(65536));
        this.y = new PVector(rng.random(65536), rng.random(65536));
    }

    @Override
    public PVector apply(PVector p, float t) {
        float s = t * frequency;
        return new PVector(
            p.x + (rng.noise(off.x + s, y.x) - 0.5f) * amplitude,
            p.y + (rng.noise(off.y + s, y.y) - 0.5f) * amplitude);
    }
}
