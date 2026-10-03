import java.util.List;

import ch.domizai.keyed.*;

Keyed<PVector> pos;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);

    pos = Keyed.ofPVector()
        .key(0, new PVector(80, 300))
        .key(1, new PVector(200, 80))
        .key(2, new PVector(320, 300))
        .key(3, new PVector(80, 300));
}

void draw() {
    background(255);

    // echo(samples, delay) returns the value now, delay seconds ago,
    // 2 * delay seconds ago, and so on: a ready-made motion trail.
    // A negative delay samples the future instead.
    List<PVector> trail = pos.echo(12, 0.04f);

    // Oldest first, so the current position is drawn on top.
    noStroke();
    for (int i = trail.size() - 1; i >= 0; i--) {
        PVector p = trail.get(i);
        fill(0, map(i, 0, trail.size(), 255, 20));
        circle(p.x, p.y, 40 - i * 2);
    }
}
