import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.lerps.*;

Keyed<Float> plain, stiff, wobbly;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));

    plain = jump();

    // Spring(lerp, frequency, damping) follows the value like a spring:
    // it lags behind, overshoots and settles. frequency is in wobbles per
    // second; damping goes from 0 (wobbles forever) to 1 (no wobble).
    stiff = jump().addEffect(new Spring<>(new FloatLerp(), 4, 0.6f));
    wobbly = jump().addEffect(new Spring<>(new FloatLerp(), 2, 0.4f));

    // For PVectors there is a shortcut: Effect.spring(2, 0.4f).
}

// Jumps right at 0.5 seconds and back at 2.5, with no motion in between.
Keyed<Float> jump() {
    return Keyed.ofFloat()
        .key(Key.at(0).hold(), 100f)
        .key(Key.at(0.5f).hold(), 300f)
        .key(2.5f, 100f);
}

void draw() {
    background(255);
    lane("hold()", 90, plain);
    lane("Spring(4, 0.6)", 210, stiff);
    lane("Spring(2, 0.4)", 330, wobbly);
}

void lane(String label, float y, Keyed<Float> x) {
    noStroke();
    fill(150);
    text(label, 40, y - 30);

    stroke(220);
    line(100, y, 300, y);

    noStroke();
    fill(0);
    circle(x.value(), y, 24);
}
