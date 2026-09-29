package ch.domizai.keyed.tween;

import processing.core.PVector;

import java.util.List;

import static processing.core.PApplet.constrain;

// Like Processing's curveVertex(): passes through every point except the first and last, which only shape the ends.
public class Spline implements Path {
    // Samples per segment; higher is more accurate.
    private static final int LUT_STEPS = 50;

    private PVector[] points;
    private float tightness = 0;
    private float t0 = 0, t1 = 1;
    // Rebuilt lazily after setters; mutating the PVectors directly won't invalidate it.
    private float[] lut;

    public Spline(PVector... points) {
        setPoints(points);
    }

    public Spline(List<PVector> points) {
        setPoints(points);
    }

    public Spline setPoints(List<PVector> points) {
        return setPoints(points.toArray(new PVector[0]));
    }

    public Spline setPoints(PVector... points) {
        if (points.length < 4) {
            throw new IllegalArgumentException("a spline needs at least 4 points, got " + points.length);
        }
        this.points = points.clone();
        lut = null;
        return this;
    }

    // Same as Processing's curveTightness(): 0 is Catmull-Rom, 1 gives straight lines.
    public Spline setTightness(float tightness) {
        this.tightness = tightness;
        lut = null;
        return this;
    }

    @Override
    public Spline slice(float t0, float t1) {
        Spline s = new Spline(points);
        s.tightness = tightness;
        s.t0 = constrain(t0, 0, 1);
        s.t1 = constrain(t1, 0, 1);
        s.lut = lut;
        return s;
    }

    @Override
    public float length() {
        float[] l = lut();
        return l[l.length - 1] * Math.abs(t1 - t0);
    }

    @Override
    public PVector value(float d) {
        return point(distToU(t0 + d * (t1 - t0)));
    }

    private int segments() {
        return points.length - 3;
    }

    // u in [0, segments()]; the integer part picks the segment.
    private PVector point(float u) {
        int i = Math.min((int) u, segments() - 1);
        float t = u - i;
        PVector p0 = points[i], p1 = points[i + 1], p2 = points[i + 2], p3 = points[i + 3];

        float k = (1 - tightness) / 2;
        float t2 = t * t, t3 = t2 * t;
        float h00 = 2 * t3 - 3 * t2 + 1;
        float h10 = t3 - 2 * t2 + t;
        float h01 = -2 * t3 + 3 * t2;
        float h11 = t3 - t2;
        return new PVector(
            h00 * p1.x + h10 * k * (p2.x - p0.x) + h01 * p2.x + h11 * k * (p3.x - p1.x),
            h00 * p1.y + h10 * k * (p2.y - p0.y) + h01 * p2.y + h11 * k * (p3.y - p1.y));
    }

    // Cumulative distances between evenly spaced samples of u.
    private float[] lut() {
        if (lut == null) {
            int n = segments() * LUT_STEPS;
            float[] l = new float[n + 1];
            PVector prev = point(0);
            for (int i = 1; i <= n; i++) {
                PVector p = point((float) i / LUT_STEPS);
                l[i] = l[i - 1] + PVector.dist(prev, p);
                prev = p;
            }
            lut = l;
        }
        return lut;
    }

    // Normalized distance (0..1) along the spline to u.
    private float distToU(float dist) {
        float[] l = lut();
        int n = l.length - 1;
        float total = l[n];
        if (total == 0) {
            return dist * segments();
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
        float ua = (lo - 1) / (float) LUT_STEPS;
        float ub = lo / (float) LUT_STEPS;
        return d1 > d0 ? ua + (dist - d0) * (ub - ua) / (d1 - d0) : ua;
    }
}
