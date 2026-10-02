package ch.domizai.keyed.types;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

// Morphs closed polygons point by point. Shapes with fewer points get extra points on their longest edges,
// so the outline stays the same and corners stay sharp. Start both shapes at the same angle to avoid twisting.
public class ShapeMorth implements Lerp<List<PVector>> {
    public List<PVector> lerp(List<PVector> a, List<PVector> b, float d) {
        if (a.isEmpty()) return copy(b);
        if (b.isEmpty()) return copy(a);

        int n = Math.max(a.size(), b.size());
        List<PVector> from = subdivide(a, n);
        List<PVector> to = subdivide(b, n);

        // New PVectors, so callers can't mutate the stored keys.
        List<PVector> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(PVector.lerp(from.get(i), to.get(i), d));
        }
        return result;
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
