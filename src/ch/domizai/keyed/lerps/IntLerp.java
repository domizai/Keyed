package ch.domizai.keyed.lerps;
import java.util.function.Function;

public class IntLerp implements Lerp<Integer> {
    /** Rounds to the nearest integer. */
    public Integer lerp(Integer a, Integer b, float d) {
        return Math.round(a + (b - a) * d);
    }

    /** f converts the unrounded blend to an integer, e.g. floor or ceil. */
    public Integer lerp(Integer a, Integer b, float d, Function<Float, Integer> f) {
        return f.apply(a + (b - a) * d);
    }
}
