package ch.domizai.keyed.tween;

import processing.core.PApplet;
import processing.core.PVector;

public class BezierTween extends PApplet implements Tween<PVector>, Cloneable {
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
        BezierTween bc = this.clone();
        bc.t0 = t0;
        bc.t1 = t1;
        return bc;
    }

    public BezierTween clone() {
        BezierTween bc = new BezierTween(p0, p1, p2, p3);
        bc.t0 = t0;
        bc.t1 = t1;
        return bc;
    }
    @Override
    public void set(PVector value) {
        this.p0 = value;
    }
    
    @Override 
    public PVector value(float d) {
        d = t0 + d * (t1 - t0);
        PVector c = new PVector(3 * (p1.x - p0.x), 3 * (p1.y - p0.y));
        PVector b = new PVector(3 * (p2.x - p1.x) - c.x, 3 * (p2.y - p1.y) - c.y);
        PVector a = new PVector(p3.x - p0.x - c.x - b.x, p3.y - p0.y - c.y - b.y);
        return new PVector(
            a.x * pow(d, 3) + b.x * pow(d, 2) + c.x * d + p0.x,
            a.y * pow(d, 3) + b.y * pow(d, 2) + c.y * d + p0.y);
    }
}
