package ch.domizai.keyed.tween;

import processing.core.PVector;

public class PVectorTween {
    public static PVector tween(PVector a, PVector b, float t) {
        return PVector.lerp(a, b, t);
    }
}
