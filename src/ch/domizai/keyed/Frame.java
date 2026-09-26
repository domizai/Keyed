package ch.domizai.keyed;

public class Frame implements Comparable<Frame> {
    private float t;

    public Frame() {
    }

    public Frame(float t) {
        this.t = t;
    }

    public static Frame at(float t) {
        return new Frame(t);
    }

    public float t() {
        return t;
    }

    public void to(float t) {
        this.t = t;
    }

    @Override
    public int compareTo(Frame o) {
        return Float.compare(this.t, o.t);
    }
}
