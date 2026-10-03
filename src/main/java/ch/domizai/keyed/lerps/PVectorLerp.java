package ch.domizai.keyed.lerps;

import processing.core.PVector;

/** Linear blend for PVectors. */
public class PVectorLerp implements Lerp<PVector> {
    public PVector lerp(PVector a, PVector b, float t) {
        return PVector.lerp(a, b, t);
    }
}
