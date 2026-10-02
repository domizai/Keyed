package ch.domizai.keyed.types;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static processing.core.PApplet.constrain;

// Text that erases back to the prefix it shares with the target, then types the rest.
// Immutable, so lerp() may return this or b directly; mutable types must return a new object.
public final class Typewriter implements Lerpable<Typewriter> {
    public final String text;

    public Typewriter(String text) {
        this.text = text;
    }

    @Override
    public Typewriter lerp(Typewriter b, float d) {
        String a = text;
        int common = 0;
        int max = min(a.length(), b.text.length());
        while (common < max && a.charAt(common) == b.text.charAt(common)) common++;

        int erase = a.length() - common;
        int type = b.text.length() - common;
        if (erase + type == 0) return b;

        // Easing may overshoot, so clamp before counting keystrokes.
        int strokes = round(constrain(d, 0, 1) * (erase + type));
        if (strokes <= erase) return new Typewriter(a.substring(0, a.length() - strokes));
        return new Typewriter(b.text.substring(0, common + strokes - erase));
    }

    @Override
    public String toString() {
        return text;
    }
}
