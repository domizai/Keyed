import ch.domizai.keyed.*;

// A reusable animation: extend Composition and create its values with add().
class Burst extends Composition {
    PApplet g;
    float x, y;
    Keyed<Float> ring, fade, spread, dotSize;

    Burst(PApplet g, float x, float y) {
        // Lasts 0.8 seconds, on a timeline of its own that plays once.
        super(0.8f);
        this.g = g;
        this.x = x;
        this.y = y;

        // add() puts a value on this composition's timeline.
        // Key times are local: 0 is when this burst was created.
        ring = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.CUBIC_OUT), 0f)
            .key(0.8f, 90f));

        fade = add(Keyed.of(0f)
            .key(0.3f, 255f)
            .key(0.8f, 0f));

        spread = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.QUART_OUT), 0f)
            .key(0.8f, 60f));

        dotSize = add(Keyed.of(0f)
            .key(0, 14f)
            .key(0.8f, 0f));
    }

    void draw() {
        g.noFill();
        g.stroke(0, fade.value());
        g.strokeWeight(4);
        g.circle(x, y, ring.value() * 2);
        g.strokeWeight(1);

        g.noStroke();
        g.fill(230, 60, 60);
        for (int i = 0; i < 8; i++) {
            float a = i * PApplet.TWO_PI / 8;
            float r = spread.value();
            g.circle(x + PApplet.cos(a) * r, y + PApplet.sin(a) * r, dotSize.value());
        }
    }
}
