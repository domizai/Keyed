package ch.domizai.keyed;

import java.util.Objects;
import ch.domizai.keyed.motion.Motion;

public class InOut<T> {
    private T value;
    private Motion<T> motion;

    public InOut(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public InOut(Motion<T> motion) {
        Objects.requireNonNull(motion);
        this.motion = motion;
    }

    public T valueIn(float t) {
        return value != null ? value : motion.valueIn(t);
    }

    public T valueOut(float t) {
        return value != null ? value : motion.valueOut(t);
    }
}