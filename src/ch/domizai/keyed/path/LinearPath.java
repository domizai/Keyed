package ch.domizai.keyed.path;

import processing.core.PApplet;
import processing.core.PVector;

public class LinearPath implements Path {
    private PVector a, b;

    public LinearPath() {
        this(new PVector(), new PVector());
    }

    public LinearPath(PVector a, PVector b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public PVector value(float d) {
        return new PVector(PApplet.lerp(a.x, b.x, d), PApplet.lerp(a.y, b.y, d));
    }
}
