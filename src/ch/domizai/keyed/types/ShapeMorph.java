package ch.domizai.keyed.types;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

// A closed polygon that morphs point by point. The shape with fewer points gets extra points on its longest edges,
// so its outline stays the same and corners stay sharp. Start both shapes at the same angle to avoid twisting.
public class ShapeMorph implements Lerpable<ShapeMorph> {
    public final List<PVector> points;

    /** Copies the points, so changing the list afterwards doesn't change this shape. */
    public ShapeMorph(List<PVector> points) {
        this.points = copy(points);
    }

    @Override
    public ShapeMorph lerp(ShapeMorph b, float d) {
        if (points.isEmpty()) return new ShapeMorph(b.points);
        if (b.points.isEmpty()) return new ShapeMorph(points);

        int n = Math.max(points.size(), b.points.size());
        List<PVector> from = subdivide(points, n);
        List<PVector> to = subdivide(b.points, n);

        List<PVector> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(PVector.lerp(from.get(i), to.get(i), d));
        }
        return new ShapeMorph(result);
    }

    /** Emits the points as vertex() calls; wrap in beginShape()/endShape(CLOSE). Uses z with a 3D renderer (P3D). */
    public void vertices(PApplet g) {
        vertices(g.g);
    }

    /** Emits the points as vertex() calls on g; uses z if g is 3D. */
    public void vertices(PGraphics g) {
        if (g.is3D()) {
            for (PVector p : points) g.vertex(p.x, p.y, p.z);
        } else {
            for (PVector p : points) g.vertex(p.x, p.y);
        }
    }

    private static List<PVector> subdivide(List<PVector> shape, int n) {
        List<PVector> pts = copy(shape);
        while (pts.size() < n) {
            int longest = 0;
            float max = -1;
            for (int i = 0; i < pts.size(); i++) {
                float len = PVector.dist(pts.get(i), pts.get((i + 1) % pts.size()));
                if (len > max) {
                    max = len;
                    longest = i;
                }
            }
            PVector mid = PVector.lerp(pts.get(longest), pts.get((longest + 1) % pts.size()), 0.5f);
            pts.add(longest + 1, mid);
        }
        return pts;
    }

    private static List<PVector> copy(List<PVector> shape) {
        List<PVector> out = new ArrayList<>(shape.size());
        for (PVector p : shape) out.add(p.copy());
        return out;
    }
}
