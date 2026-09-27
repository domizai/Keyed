package ch.domizai.keyed;

public class Pin implements Comparable<Pin> {
    private float t;

    public Pin() {
    }

    public Pin(float t) {
        this.t = t;
    }

    public static Pin at(float t) {
        return new Pin(t);
    }

    public float t() {
        return t;
    }

    public void to(float t) {
        this.t = t;
    }

    @Override
    public int compareTo(Pin o) {
        return Float.compare(this.t, o.t);
    }
}
