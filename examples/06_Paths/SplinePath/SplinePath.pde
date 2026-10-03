import ch.domizai.keyed.*;
import ch.domizai.keyed.tween.*;

PVector[] pts = {
    new PVector(200, 60),
    new PVector(340, 170),
    new PVector(290, 330),
    new PVector(110, 330),
    new PVector(60, 170)
};

Spline spline;
Keyed<PVector> pos;
float tightness = 0;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(5);
    textFont(createFont("Courier", 14));

    // Like curveVertex(): the curve passes through every point except
    // the first and last, which only shape its ends. Wrapping the points
    // around like this closes the loop.
    spline = new Spline(pts[4], pts[0], pts[1], pts[2], pts[3], pts[4], pts[0], pts[1]);

    pos = Keyed.of(new PVector())
        .key(0, spline)
        .key(5, spline);
}

void mouseMoved() {
    // Same as curveTightness(): 0 is round, 1 gives straight lines.
    tightness = constrain(map(mouseX, 40, 360, 0, 1), 0, 1);
    spline.setTightness(tightness);
}

void draw() {
    background(255);

    noFill();
    stroke(200);
    beginShape();
    for (int i = 0; i <= 200; i++) {
        PVector v = spline.value(i / 200f);
        vertex(v.x, v.y);
    }
    endShape();

    noStroke();
    fill(200);
    for (PVector p : pts) {
        circle(p.x, p.y, 10);
    }

    PVector p = pos.value();
    fill(0);
    circle(p.x, p.y, 24);

    fill(150);
    text("tightness " + nf(tightness, 1, 2) + "   move the mouse", 40, 380);
}
