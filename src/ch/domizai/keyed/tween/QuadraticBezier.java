package ch.domizai.keyed.tween;

import processing.core.PVector;

import static processing.core.PApplet.constrain;

public class QuadraticBezier implements Tween<PVector> {
    // Higher is more accurate.
    private static final int LUT_STEPS = 50;

    private PVector p0, p1, p2;
    private float t0 = 0, t1 = 1;
    // Rebuilt lazily after setters; mutating the PVectors directly won't invalidate it.
    private float[] lut;

    public QuadraticBezier(PVector p0, PVector p1, PVector p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    public QuadraticBezier setP0(PVector p0) {
        this.p0 = p0;
        lut = null;
        return this;
    }

    public QuadraticBezier setP1(PVector p1) {
        this.p1 = p1;
        lut = null;
        return this;
    }

    public QuadraticBezier setP2(PVector p2) {
        this.p2 = p2;
        lut = null;
        return this;
    }

    public QuadraticBezier setCurve(PVector p0, PVector p1, PVector p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        lut = null;
        return this;
    }

    public QuadraticBezier slice(float t0, float t1) {
        QuadraticBezier bc = new QuadraticBezier(p0, p1, p2);
        bc.t0 = constrain(t0, 0, 1);
        bc.t1 = constrain(t1, 0, 1);
        bc.lut = lut;
        return bc;
    }

    @Override
    public PVector value(float d) {
        return point(distToT(t0 + d * (t1 - t0)));
    }

    private PVector point(float t) {
        float u = 1 - t;
        float b0 = u * u;
        float b1 = 2 * u * t;
        float b2 = t * t;
        return new PVector(
            b0 * p0.x + b1 * p1.x + b2 * p2.x,
            b0 * p0.y + b1 * p1.y + b2 * p2.y);
    }

    // Cumulative distances between LUT_STEPS + 1 evenly spaced samples of t.
    private float[] lut() {
        if (lut == null) {
            float[] l = new float[LUT_STEPS + 1];
            PVector prev = point(0);
            for (int i = 1; i <= LUT_STEPS; i++) {
                PVector p = point((float) i / LUT_STEPS);
                l[i] = l[i - 1] + PVector.dist(prev, p);
                prev = p;
            }
            lut = l;
        }
        return lut;
    }

    // Normalized distance (0..1) along the curve to the curve parameter t.
    private float distToT(float dist) {
        float[] l = lut();
        int n = l.length - 1;
        float total = l[n];
        if (total == 0) {
            return dist;
        }
        dist = constrain(dist * total, 0, total);

        int lo = 1, hi = n;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (l[mid] < dist) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }

        float d0 = l[lo - 1], d1 = l[lo];
        float ta = (lo - 1) / (float) n;
        float tb = lo / (float) n;
        return d1 > d0 ? ta + (dist - d0) * (tb - ta) / (d1 - d0) : ta;
    }
}
