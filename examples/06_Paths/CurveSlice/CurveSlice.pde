import ch.domizai.keyed.*;
import ch.domizai.keyed.tween.*;

Curve curve;
Path reversed;
Keyed<Float> progress;
Keyed<PVector> back;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));

    // add() chains paths into a Curve, played one after another at even speed.
    // Each path starts where the previous one ends.
    curve = new CubicBezier(
            new PVector(60, 330), new PVector(60, 200),
            new PVector(180, 220), new PVector(180, 140))
        .add(new QuadraticBezier(
            new PVector(180, 140), new PVector(180, 50), new PVector(260, 60)))
        .add(new CubicBezier(
            new PVector(260, 60), new PVector(360, 70),
            new PVector(370, 220), new PVector(300, 300)));

    // Draws the curve on in 3 seconds, then holds for 1.
    progress = Keyed.of(0f)
        .key(Key.at(0).setEasing(1 / 3f), 0f)
        .key(Key.at(3).setEasing(1 / 3f), 1f);

    // slice(t0, t1) is the part of a path between t0 and t1.
    // With t1 < t0 it runs backwards.
    reversed = curve.slice(1, 0);
    back = Keyed.of(new PVector())
        .key(0, reversed)
        .key(4, reversed);
}

void draw() {
    background(255);

    drawPath(curve, 220, 1);

    // Only the first part of the curve, growing with progress.
    float p = progress.value();
    drawPath(curve.slice(0, p), 0, 4);
    PVector tip = curve.value(p);
    noStroke();
    fill(0);
    circle(tip.x, tip.y, 12);

    PVector b = back.value();
    fill(230, 60, 60);
    circle(b.x, b.y, 24);

    fill(0);
    text("slice(0, progress)", 40, 360);
    fill(230, 60, 60);
    text("slice(1, 0)", 40, 380);
}

void drawPath(Path path, int col, float weight) {
    noFill();
    stroke(col);
    strokeWeight(weight);
    beginShape();
    for (int i = 0; i <= 150; i++) {
        PVector v = path.value(i / 150f);
        vertex(v.x, v.y);
    }
    endShape();
    strokeWeight(1);
}
