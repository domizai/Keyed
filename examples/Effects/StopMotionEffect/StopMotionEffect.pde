import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;

Keyed<Float> smooth, onTwos, choppy;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(2);
    textFont(createFont("Courier", 14));

    smooth = sweep();

    // StopMotion(step) holds each pose for step seconds, whatever the keys.
    // 2f / 24 is "on twos" at 24 fps, common in hand-drawn animation.
    onTwos = sweep().addEffect(new StopMotion<>(2f / 24));
    choppy = sweep().addEffect(new StopMotion<>(0.25f));

    // Unlike Key.hold(), which holds single keys,
    // StopMotion samples the whole animation at a fixed rate.
}

Keyed<Float> sweep() {
    return Keyed.of(0f)
        .key(Key.at(0).setEasing(1 / 3f), 80f)
        .key(Key.at(1).setEasing(1 / 3f), 320f)
        .key(Key.at(2).setEasing(1 / 3f), 80f);
}

void draw() {
    background(255);
    lane("no effect", 90, smooth);
    lane("StopMotion(2f / 24)", 210, onTwos);
    lane("StopMotion(0.25f)", 330, choppy);
}

void lane(String label, float y, Keyed<Float> x) {
    noStroke();
    fill(150);
    text(label, 40, y - 30);

    stroke(220);
    line(80, y, 320, y);

    noStroke();
    fill(0);
    circle(x.value(), y, 24);
}
