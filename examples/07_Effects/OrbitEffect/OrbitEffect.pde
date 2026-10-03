import java.util.List;

import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;

Keyed<PVector> center, ball;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);

    center = motion();

    // Orbit(radius, frequency) circles around the value, frequency times
    // per second. 2 per second in a 4 second loop is a whole number of
    // circles, so the loop is seamless.
    // Orbit(radius, frequency, axis) circles around any 3D axis instead.
    ball = motion().addEffect(new Orbit(30, 2));
}

Keyed<PVector> motion() {
    return Keyed.of(new PVector())
        .key(Key.at(0).setEasing(1 / 3f), new PVector(100, 200))
        .key(Key.at(2).setEasing(1 / 3f), new PVector(300, 200))
        .key(Key.at(4).setEasing(1 / 3f), new PVector(100, 200));
}

void draw() {
    background(255);

    // Where the ball has been, from echo().
    List<PVector> trail = ball.echo(60, 0.015f);
    noStroke();
    for (int i = trail.size() - 1; i > 0; i--) {
        PVector p = trail.get(i);
        fill(0, map(i, 0, trail.size(), 120, 0));
        circle(p.x, p.y, 6);
    }

    // The value without the orbit.
    PVector c = center.value();
    PVector p = ball.value();
    stroke(220);
    line(c.x, c.y, p.x, p.y);
    noStroke();
    fill(200);
    circle(c.x, c.y, 8);

    fill(0);
    circle(p.x, p.y, 24);
}
