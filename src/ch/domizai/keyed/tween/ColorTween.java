package ch.domizai.keyed.tween;

import processing.core.PApplet;

public class ColorTween extends PApplet implements Tween<Integer> {
    @Override
    public Integer lerp(Integer a, Integer b, float t) {
        return lerpColor(a, b, t);
    }
}
