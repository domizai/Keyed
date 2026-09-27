package ch.domizai.keyed.tween;

import processing.core.PVector;

public class PVectorTween implements Tween<PVector> {

    private PVector p;

    public PVectorTween() {
        this.p = new PVector();
    }

    public PVectorTween(PVector p) {
        this.p = p;
    }

    @Override
    public PVectorTween clone() {
        return new PVectorTween(p.copy());
    }

    @Override 
    public void set(PVector value) {
        this.p = value.copy();
    }

    @Override
    public PVector value(float t) {
        return p.copy();
    }
}
