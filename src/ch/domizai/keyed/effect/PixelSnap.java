package ch.domizai.keyed.effect;

import processing.core.PVector;

public class PixelSnap implements Effect<PVector> {
    private final float size;

    /** Snaps x and y to multiples of size; z is untouched. */
    public PixelSnap(float size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0, was " + size);
        }
        this.size = size;
    }

    @Override
    public PVector apply(PVector p, float t) {
        return new PVector(Math.round(p.x / size) * size, Math.round(p.y / size) * size, p.z);
    }
}
