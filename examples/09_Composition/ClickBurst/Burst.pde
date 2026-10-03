import ch.domizai.keyed.*;

// A reusable animation: extend Composition and create its values with add().
// Constructor parameters make every instance a little different.
class Burst extends Composition {
    PApplet g;
    float x, y;
    int col;
    Keyed<Float> ring, fade, spread, dotSize;

    // size scales the burst, col colors its dots, and duration is how long it plays.
    Burst(PApplet g, float x, float y, float size, int col, float duration) {
        // A timeline of its own that plays once.
        super(duration);
        this.g = g;
        this.x = x;
        this.y = y;
        this.col = col;

        // add() puts a value on this composition's timeline.
        // Key times are local: 0 is when this burst was created.
        // Here they are fractions of the duration, so the timing stretches with it.
        float d = duration;

        ring = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.CUBIC_OUT), 0f)
            .key(d, 90f * size));

        fade = add(Keyed.of(0f)
            .key(0.4f * d, 255f)
            .key(d, 0f));

        spread = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.QUART_OUT), 0f)
            .key(d, 60f * size));

        dotSize = add(Keyed.of(0f)
            .key(0, 14f * size)
            .key(d, 0f));
    }

    void draw() {
        g.noFill();
        g.stroke(0, fade.value());
        g.strokeWeight(4);
        g.circle(x, y, ring.value() * 2);
        g.strokeWeight(1);

        g.noStroke();
        g.fill(col);
        for (int i = 0; i < 8; i++) {
            float a = i * PApplet.TWO_PI / 8;
            float r = spread.value();
            g.circle(x + PApplet.cos(a) * r, y + PApplet.sin(a) * r, dotSize.value());
        }
    }
}
