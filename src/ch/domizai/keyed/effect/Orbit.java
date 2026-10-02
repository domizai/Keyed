package ch.domizai.keyed.effect;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Orbit implements Effect<PVector> {
    private final float radius, frequency; // frequency in cycles per second

    public Orbit(float radius, float frequency) {
        this.radius = radius;
        this.frequency = frequency;
    }

    @Override
    public PVector apply(PVector p, float t) {
        float a = t * frequency * PConstants.TWO_PI;
        return new PVector(p.x + PApplet.cos(a) * radius, p.y + PApplet.sin(a) * radius, p.z);
    }
}
