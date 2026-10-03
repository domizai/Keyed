package ch.domizai.keyed.examples.customtypes;

import processing.core.PApplet;

import static processing.core.PConstants.PI;
import static processing.core.PConstants.TWO_PI;

import ch.domizai.keyed.types.Lerpable;

// Immutable position, rotation and scale, so lerp() can safely share instances.
public class Transform implements Lerpable<Transform> {
    public final float x, y, rotation, scale;

    /** Rotation in radians. */
    public Transform(float x, float y, float rotation, float scale) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.scale = scale;
    }

    /** Position only, no rotation, scale 1. */
    public Transform(float x, float y) {
        this(x, y, 0, 1);
    }

    /** Rotation takes the shorter way round, e.g. 350° to 10° turns 20°, not 340°. */
    @Override
    public Transform lerp(Transform b, float d) {
        float turn = ((b.rotation - rotation) % TWO_PI + TWO_PI + PI) % TWO_PI - PI;
        return new Transform(
            PApplet.lerp(x, b.x, d),
            PApplet.lerp(y, b.y, d),
            rotation + turn * d,
            PApplet.lerp(scale, b.scale, d));
    }

    /** Translates, rotates and scales g; call inside pushMatrix()/popMatrix(), e.g. transform.value().apply(this). */
    public void apply(PApplet g) {
        g.translate(x, y);
        g.rotate(rotation);
        g.scale(scale);
    }
}
