package ch.domizai.keyed.tween;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static processing.core.PApplet.constrain;

// Paths played one after another at even speed; gaps between a path's end and the next start are jumped.
public class Curve implements Path {
    private final List<Path> paths;
    private float t0 = 0, t1 = 1;

    public Curve(Path... paths) {
        this(Arrays.asList(paths));
    }

    private Curve(List<Path> paths) {
        if (paths.isEmpty()) {
            throw new IllegalArgumentException("a curve needs at least one path");
        }
        this.paths = new ArrayList<>(paths);
    }

    // Returns a new curve; this one is unchanged.
    @Override
    public Curve add(Path next) {
        if (t0 != 0 || t1 != 1) {
            return new Curve(this, next);
        }
        Curve c = new Curve(paths);
        c.paths.add(next);
        return c;
    }

    @Override
    public Curve slice(float t0, float t1) {
        Curve c = new Curve(paths);
        c.t0 = constrain(t0, 0, 1);
        c.t1 = constrain(t1, 0, 1);
        return c;
    }

    @Override
    public float length() {
        return fullLength() * Math.abs(t1 - t0);
    }

    @Override
    public PVector value(float d) {
        // Lengths are read on every call because paths can change through their setters.
        float dist = constrain(t0 + d * (t1 - t0), 0, 1) * fullLength();
        int last = paths.size() - 1;
        for (int i = 0; i < last; i++) {
            float len = paths.get(i).length();
            if (dist <= len) {
                return paths.get(i).value(len > 0 ? dist / len : 0);
            }
            dist -= len;
        }
        float len = paths.get(last).length();
        return paths.get(last).value(len > 0 ? constrain(dist / len, 0, 1) : 0);
    }

    private float fullLength() {
        float total = 0;
        for (Path p : paths) {
            total += p.length();
        }
        return total;
    }
}
