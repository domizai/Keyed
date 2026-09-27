package ch.domizai.keyed.tween;

import processing.core.PVector;

public class BezierTween implements Tween<PVector> {
    private PVector p0, p1, p2, p3;
    private float t0 = 0, t1 = 1;

    public BezierTween(PVector p0, PVector p1, PVector p2, PVector p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
    
    public BezierTween setP0(PVector p0) {
        this.p0 = p0;
        return this;
    }

    public BezierTween setP1(PVector p1) {
        this.p1 = p1;
        return this;
    }

    public BezierTween setP2(PVector p2) {
        this.p2 = p2;
        return this;
    }

    public BezierTween setP3(PVector p3) {
        this.p3 = p3;
        return this;
    }

    public BezierTween setCurve(PVector p0, PVector p1, PVector p2, PVector p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        return this;
    }

    public BezierTween slice(float t0, float t1) {
        t0 = t0 > 1 ? t0 = 1 : t0 < -1 ? t0 = -1 : t0;
        t1 = t1 > 1 ? t1 = 1 : t1 < -1 ? t1 = -1 : t1;
        BezierTween bc = new BezierTween(p0, p1, p2, p3);
        bc.t0 = t0;
        bc.t1 = t1;
        return bc;
    }

    @Override 
    public PVector value(float d) {
        d = t0 + d * (t1 - t0);
        float u = 1 - d;
        float b0 = u * u * u;
        float b1 = 3 * u * u * d;
        float b2 = 3 * u * d * d;
        float b3 = d * d * d;
        return new PVector(
            b0 * p0.x + b1 * p1.x + b2 * p2.x + b3 * p3.x,
            b0 * p0.y + b1 * p1.y + b2 * p2.y + b3 * p3.y);
    }
}
