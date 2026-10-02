package ch.domizai.keyed.effect;

import processing.core.PVector;

// Smooth random offset, like After Effects' wiggle(); the offset per axis stays within ±amplitude.
public class Wiggle implements Effect<PVector> {
    private static long nextSeed = 1;

    private final PVector amplitude;
    private final float frequency;
    private final long seed;

    // Wiggles x and y only.
    public Wiggle(float amplitude, float frequency) {
        this(new PVector(amplitude, amplitude, 0), frequency);
    }

    // Amplitude per axis; set z to wiggle in 3D.
    public Wiggle(PVector amplitude, float frequency) {
        this(amplitude, frequency, nextSeed++);
    }

    // The same seed and settings always give the same motion; without one, each Wiggle gets its own.
    public Wiggle(PVector amplitude, float frequency, long seed) {
        this.amplitude = amplitude.copy();
        this.frequency = frequency;
        this.seed = seed;
    }

    @Override
    public PVector apply(PVector p, float t) {
        float s = t * frequency;
        return new PVector(
            p.x + noise(s, 0) * amplitude.x,
            p.y + noise(s, 1) * amplitude.y,
            p.z + noise(s, 2) * amplitude.z);
    }

    // 1D gradient noise in [-1, 1].
    private float noise(float x, int axis) {
        long i = (long) Math.floor(x);
        float f = x - i;
        float g0 = gradient(i, axis) * f;
        float g1 = gradient(i + 1, axis) * (f - 1);
        float u = f * f * f * (f * (f * 6 - 15) + 10);
        return 2 * (g0 + u * (g1 - g0));
    }

    // Pseudo-random slope in [-1, 1) for lattice point i, hashed with SplitMix64.
    private float gradient(long i, int axis) {
        long h = i * 0x9E3779B97F4A7C15L + seed * 0xBF58476D1CE4E5B9L + axis * 0x94D049BB133111EBL;
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        h ^= h >>> 31;
        return (h >>> 40) / (float) (1L << 23) - 1;
    }
}
