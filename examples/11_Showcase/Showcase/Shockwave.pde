
import ch.domizai.keyed.*;

// One impact: a ring and a spray of sparks, on a one-shot timeline of its own.
class Shockwave extends Composition {
    static final float LIFE = 1.4f;

    PApplet g;
    int col;
    Keyed<Float> radius, alpha, weight, spray, flash;

    Shockwave(PApplet g, int col) {
        super(LIFE);
        this.g = g;
        this.col = col;

        radius = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.cubicBezier(0.1f, 0.9f, 0.2f, 1)), 10f)
            .key(LIFE, 440f));

        alpha = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.QUAD_IN), 255f)
            .key(LIFE, 0f));

        weight = add(Keyed.of(0f)
            .key(0, 14f)
            .key(LIFE, 1f));

        spray = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.QUART_OUT), 0f)
            .key(LIFE, 340f));

        flash = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.CUBIC_OUT), 1f)
            .key(0.4f, 0f));
    }

    // Drawn in the scene's space, so the ring lies in the shape's plane.
    void draw() {
        float a = alpha.value();
        g.noFill();
        g.stroke(col, a);
        g.strokeWeight(weight.value());
        g.circle(0, 0, radius.value() * 2);

        float r = spray.value();
        g.strokeWeight(5);
        for (int i = 0; i < 24; i++) {
            float ang = i * TWO_PI / 24;
            float lift = sin(i * 2.4f) * r * 0.5f;
            float rr = r * (0.6f + 0.4f * abs(sin(i * 1.7f)));
            g.stroke(255, a * 0.8f);
            g.point(cos(ang) * rr, sin(ang) * rr, lift);
        }
    }
}
