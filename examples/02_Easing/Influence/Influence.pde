import ch.domizai.keyed.*;

float x0 = 40, x1 = 360;

Keyed<Float> linear, easy, strong, easeOut, easeIn;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));
    textAlign(LEFT, CENTER);

    // Without easing the speed is constant and stops are abrupt.
    linear = Keyed.of(0f)
        .key(0, x0)
        .key(1.5f, x1)
        .key(3, x0);

    // Influence in [0, 1] slows the motion near a key, like in After Effects.
    // 1/3 on both sides of a key is After Effects' Easy Ease.
    easy = Keyed.of(0f)
        .key(Key.at(0).setEasing(1 / 3f), x0)
        .key(Key.at(1.5f).setEasing(1 / 3f), x1)
        .key(Key.at(3).setEasing(1 / 3f), x0);

    // More influence: slower near the keys, faster in between.
    strong = Keyed.of(0f)
        .key(Key.at(0).setEasing(1), x0)
        .key(Key.at(1.5f).setEasing(1), x1)
        .key(Key.at(3).setEasing(1), x0);

    // setEasingOut() only eases leaving a key: soft start, hard stop.
    easeOut = Keyed.of(0f)
        .key(Key.at(0).setEasingOut(1), x0)
        .key(Key.at(1.5f).setEasingOut(1), x1)
        .key(Key.at(3).setEasingOut(1), x0);

    // setEasingIn() only eases arriving at a key: hard start, soft stop.
    easeIn = Keyed.of(0f)
        .key(Key.at(0).setEasingIn(1), x0)
        .key(Key.at(1.5f).setEasingIn(1), x1)
        .key(Key.at(3).setEasingIn(1), x0);
}

void draw() {
    background(255);
    lane("linear", 70, linear);
    lane("setEasing(1 / 3f)", 135, easy);
    lane("setEasing(1)", 200, strong);
    lane("setEasingOut(1)", 265, easeOut);
    lane("setEasingIn(1)", 330, easeIn);
}

void lane(String label, float y, Keyed<Float> x) {
    noStroke();
    fill(150);
    text(label, x0, y - 20);

    stroke(220);
    line(x0, y, x1, y);

    noStroke();
    fill(0);
    circle(x.value(), y, 16);
}
