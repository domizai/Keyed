package ch.domizai.keyed.effect;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Orbit implements Effect<PVector> {
    private final float radius, frequency; // frequency in cycles per second
    // Orthonormal basis of the orbit plane.
    private final PVector u, v;

    // Orbits in the XY plane.
    public Orbit(float radius, float frequency) {
        this(radius, frequency, new PVector(0, 0, 1));
    }

    // Orbits in the plane perpendicular to axis; the direction follows the right-hand rule.
    public Orbit(float radius, float frequency, PVector axis) {
        this.radius = radius;
        this.frequency = frequency;
        PVector n = axis.copy().normalize();
        PVector ref = Math.abs(n.y) < 0.9f ? new PVector(0, 1, 0) : new PVector(1, 0, 0);
        this.u = ref.cross(n).normalize();
        this.v = n.cross(u);
    }

    @Override
    public PVector apply(PVector p, float t) {
        float a = t * frequency * PConstants.TWO_PI;
        float c = PApplet.cos(a) * radius, s = PApplet.sin(a) * radius;
        return new PVector(
            p.x + u.x * c + v.x * s,
            p.y + u.y * c + v.y * s,
            p.z + u.z * c + v.z * s);
    }
}
