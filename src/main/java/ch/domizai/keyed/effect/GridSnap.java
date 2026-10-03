package ch.domizai.keyed.effect;

import processing.core.PVector;

/** Snaps x and y to a grid. */
public class GridSnap implements Effect<PVector> {
    private final float sizeX, sizeY;

    /** Snaps x and y to multiples of size; z is untouched. */
    public GridSnap(float size) {
        this(requirePositive(size), size);
    }

    /** Snaps x to multiples of sizeX and y to multiples of sizeY; 0 leaves that axis untouched, z is never snapped. */
    public GridSnap(float sizeX, float sizeY) {
        this.sizeX = checkSize("sizeX", sizeX);
        this.sizeY = checkSize("sizeY", sizeY);
        if (sizeX == 0 && sizeY == 0) {
            throw new IllegalArgumentException("at least one size must be > 0");
        }
    }

    @Override
    public PVector apply(PVector p, float t) {
        return new PVector(snap(p.x, sizeX), snap(p.y, sizeY), p.z);
    }

    // Rounds half up like Math.round, without its int overflow for large v / size.
    private static float snap(float v, float size) {
        return size > 0 ? (float) Math.floor(v / size + 0.5f) * size : v;
    }

    // Also rejects NaN, which fails every comparison.
    private static float checkSize(String name, float size) {
        if (!(size >= 0) || Float.isInfinite(size)) {
            throw new IllegalArgumentException(name + " must be >= 0 and finite, was " + size);
        }
        return size;
    }

    private static float requirePositive(float size) {
        if (!(size > 0) || Float.isInfinite(size)) {
            throw new IllegalArgumentException("size must be > 0 and finite, was " + size);
        }
        return size;
    }
}
