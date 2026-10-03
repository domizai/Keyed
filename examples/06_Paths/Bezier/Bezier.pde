import ch.domizai.keyed.*;
import ch.domizai.keyed.tween.*;

PVector[] c = {
    new PVector(60, 140), new PVector(120, 50),
    new PVector(280, 190), new PVector(340, 100)
};
PVector[] q = {
    new PVector(60, 340), new PVector(200, 190), new PVector(340, 340)
};

CubicBezier cubic;
QuadraticBezier quad;
Keyed<PVector> a, b;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));

    cubic = new CubicBezier(c[0], c[1], c[2], c[3]);
    quad = new QuadraticBezier(q[0], q[1], q[2]);

    // A key can follow a path instead of holding a fixed value.
    // Two keys on the same path travel along all of it, start to end.
    a = Keyed.of(new PVector())
        .key(0, cubic)
        .key(3, cubic);

    // at() pins a key to one position on the path: 0 is the start, 1 the end.
    // From at(0) to at(1) and back to at(0) goes there and back again.
    // Easing works as usual, shaping the progress along the path.
    b = Keyed.of(new PVector())
        .key(Key.at(0).setEasing(1 / 3f), quad.at(0))
        .key(Key.at(1.5f).setEasing(1 / 3f), quad.at(1))
        .key(Key.at(3).setEasing(1 / 3f), quad.at(0));
}

void draw() {
    background(255);

    handle(c[0], c[1]);
    handle(c[3], c[2]);
    handle(q[0], q[1]);
    handle(q[2], q[1]);

    drawPath(cubic);
    drawPath(quad);

    noStroke();
    fill(0);
    PVector pa = a.value();
    circle(pa.x, pa.y, 24);
    PVector pb = b.value();
    circle(pb.x, pb.y, 24);

    fill(150);
    text("CubicBezier", 40, 30);
    text("QuadraticBezier with at()", 40, 380);
}

void drawPath(Path p) {
    // A path is a Tween<PVector>: value(d) is the point at d, from 0 to 1.
    noFill();
    stroke(200);
    beginShape();
    for (int i = 0; i <= 100; i++) {
        PVector v = p.value(i / 100f);
        vertex(v.x, v.y);
    }
    endShape();

    // Paths are traveled at constant speed, so even steps in d are evenly spaced.
    noStroke();
    fill(200);
    for (int i = 0; i <= 10; i++) {
        PVector v = p.value(i / 10f);
        circle(v.x, v.y, 6);
    }
}

void handle(PVector anchor, PVector control) {
    stroke(230);
    line(anchor.x, anchor.y, control.x, control.y);
    noStroke();
    fill(230);
    rectMode(CENTER);
    rect(control.x, control.y, 8, 8);
    rectMode(CORNER);
}
