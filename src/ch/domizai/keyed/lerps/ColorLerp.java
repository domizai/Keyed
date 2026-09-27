package ch.domizai.keyed.lerps;

import processing.core.PApplet;

public class ColorLerp extends PApplet implements Lerp<Integer> {
    public Integer lerp(Integer a, Integer b, float t) {
        return lerpColor(a, b, t);
    }
}
