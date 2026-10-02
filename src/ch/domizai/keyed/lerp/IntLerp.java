package ch.domizai.keyed.lerp;
import java.util.function.Function;

public class IntLerp implements Lerp<Integer> {
    public Integer lerp(Integer a, Integer b, float d) {
        return Math.round(a + (b - a) * d);
    }

    public Integer lerp(Integer a, Integer b, float d, Function<Float, Integer> f) {
        return f.apply(a + (b - a) * d);
    }
}
