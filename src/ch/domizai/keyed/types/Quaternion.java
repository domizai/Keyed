package ch.domizai.keyed.types;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

// An immutable 3D rotation. Blends with slerp along the shortest arc, without gimbal lock.
public final class Quaternion implements Lerpable<Quaternion> {
    public final float w, x, y, z;

    /** Normalized to unit length. */
    public Quaternion(float w, float x, float y, float z) {
        float m = (float) Math.sqrt(w * w + x * x + y * y + z * z);
        this.w = w / m;
        this.x = x / m;
        this.y = y / m;
        this.z = z / m;
    }

    /** No rotation. */
    public static Quaternion identity() {
        return new Quaternion(1, 0, 0, 0);
    }

    /** Angle in radians, counterclockwise around axis like Processing's rotate(angle, x, y, z). */
    public static Quaternion fromAxisAngle(PVector axis, float angle) {
        PVector n = axis.copy().normalize();
        float s = (float) Math.sin(angle / 2);
        return new Quaternion((float) Math.cos(angle / 2), n.x * s, n.y * s, n.z * s);
    }

    /** Combines rotations: applies o first, then this. */
    public Quaternion mult(Quaternion o) {
        return new Quaternion(
            w * o.w - x * o.x - y * o.y - z * o.z,
            w * o.x + x * o.w + y * o.z - z * o.y,
            w * o.y - x * o.z + y * o.w + z * o.x,
            w * o.z + x * o.y - y * o.x + z * o.w);
    }

    /** Rotation angle in radians, in [0, 2π]. */
    public float angle() {
        return 2 * (float) Math.acos(Math.max(-1, Math.min(1, w)));
    }

    /** Unit rotation axis; x axis when there is no rotation. */
    public PVector axis() {
        float s = (float) Math.sqrt(Math.max(0, 1 - w * w));
        return s < 1e-6f ? new PVector(1, 0, 0) : new PVector(x / s, y / s, z / s);
    }

    /** Rotates the sketch's matrix by this rotation. */
    public void apply(PApplet g) {
        apply(g.g);
    }

    /** Rotates g's matrix by this rotation. */
    public void apply(PGraphics g) {
        PVector a = axis();
        g.rotate(angle(), a.x, a.y, a.z);
    }

    @Override
    public Quaternion lerp(Quaternion b, float d) {
        float dot = w * b.w + x * b.x + y * b.y + z * b.z;
        // q and -q are the same rotation; flipping one takes the shorter way.
        float s = dot < 0 ? -1 : 1;
        dot *= s;

        float ka, kb;
        if (dot > 0.9995f) {
            ka = 1 - d;
            kb = d;
        } else {
            float theta = (float) Math.acos(dot);
            float sin = (float) Math.sin(theta);
            ka = (float) Math.sin((1 - d) * theta) / sin;
            kb = (float) Math.sin(d * theta) / sin;
        }
        kb *= s;
        return new Quaternion(
            ka * w + kb * b.w,
            ka * x + kb * b.x,
            ka * y + kb * b.y,
            ka * z + kb * b.z);
    }

    @Override
    public String toString() {
        return "[ " + w + ", " + x + ", " + y + ", " + z + " ]";
    }
}
