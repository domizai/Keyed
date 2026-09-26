package ch.domizai.keyed.motion;

public interface Motion<T> {
    T valueIn(float d);
    T valueOut(float d);
}
