package ch.domizai.keyed.lerp;

import processing.core.PVector;

public class PVectorLerp implements Lerp<PVector> {
    public PVector lerp(PVector a, PVector b, float t) {
        return PVector.lerp(a, b, t);
    }
}
