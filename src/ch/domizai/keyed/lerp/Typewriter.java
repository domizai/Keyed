package ch.domizai.keyed.lerp;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static processing.core.PApplet.constrain;

// Custom Lerp example: erases a back to the prefix it shares with b, then types the rest of b.
// Strings are immutable, so returning a or b directly is safe; mutable types must return a new object.
public class Typewriter implements Lerp<String> {
    public String lerp(String a, String b, float d) {
        int common = 0;
        int max = min(a.length(), b.length());
        while (common < max && a.charAt(common) == b.charAt(common)) common++;

        int erase = a.length() - common;
        int type = b.length() - common;
        if (erase + type == 0) return b;

        // Easing may overshoot, so clamp before counting keystrokes.
        int strokes = round(constrain(d, 0, 1) * (erase + type));
        if (strokes <= erase) return a.substring(0, a.length() - strokes);
        return b.substring(0, common + strokes - erase);
    }
}
