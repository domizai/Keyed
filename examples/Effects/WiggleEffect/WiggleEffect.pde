import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;

Keyed<PVector> gentle, jittery, vertical;

void settings() {
    size(400, 400);
}

void setup() {
    // No duration: the default timeline runs forever, and so does the wiggle.
    Keyed.init(this);
    textFont(createFont("Courier", 14));
    textAlign(LEFT, CENTER);

    // Effects change the value on top of the keys. Without any keys
    // the value is just the default, and the effect still moves it.

    // Wiggle(amplitude, frequency): a smooth random offset of up to
    // ±amplitude, changing direction about frequency times per second.
    gentle = Keyed.of(new PVector(300, 80))
        .addEffect(new Wiggle(10, 1));

    jittery = Keyed.of(new PVector(300, 200))
        .addEffect(new Wiggle(30, 3));

    // An amplitude per axis: this one only moves up and down.
    vertical = Keyed.of(new PVector(300, 320))
        .addEffect(new Wiggle(new PVector(0, 30), 2));

    // Every Wiggle moves differently. Pass a seed as a third argument,
    // and Wiggles with the same seed and settings move the same way.
    // On a looping timeline, Wiggle loops seamlessly when
    // duration * frequency is a whole number.
}

void draw() {
    background(255);
    row("Wiggle(10, 1)", 80, gentle);
    row("Wiggle(30, 3)", 200, jittery);
    row("Wiggle(new PVector(0, 30), 2)", 320, vertical);
}

void row(String label, float y, Keyed<PVector> ball) {
    noStroke();
    fill(150);
    text(label, 20, y);

    // The value without the wiggle.
    fill(220);
    circle(300, y, 8);

    PVector p = ball.value();
    fill(0);
    circle(p.x, p.y, 24);
}
