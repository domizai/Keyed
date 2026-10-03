package ch.domizai.keyed.lerps;

import processing.core.PConstants;

/** Blends angles in radians the short way round, e.g. 350° to 10° turns 20°, not 340°. */
public class AngleLerp implements Lerp<Float> {
    public Float lerp(Float a, Float b, float t) {
        float diff = ((b - a) % PConstants.TWO_PI + PConstants.TWO_PI + PConstants.PI) % PConstants.TWO_PI - PConstants.PI;
        return a + diff * t;
    }
}
