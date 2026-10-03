package ch.domizai.keyed.lerps;

import processing.core.PApplet;
import processing.core.PConstants;

public class ColorLerp implements Lerp<Integer> {
    public Integer lerp(Integer a, Integer b, float t) {
        return PApplet.lerpColor(a, b, t, PConstants.RGB);
    }
}
