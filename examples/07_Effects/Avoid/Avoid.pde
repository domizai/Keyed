import java.util.List;

import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.tween.*;

int points = 6;
float radius = 80;
float softness = 100;

List<PVector> loop;
Spline spline;
Keyed<PVector> pos;
PVector obstacle;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));

    // The same path on every run. Click for a new one.
    randomSeed(4);
    loop = createLoop(points);
    spline = new Spline(loop);

    pos = Keyed.ofPVector()
        .key(0, spline)
        .key(4, spline);

    obstacle = new PVector(width / 2f, height / 2f);
    pos.addEffect(avoid(obstacle, radius, softness));
}

// A custom effect that keeps the value out of a circle.
// Points inside are pushed onto its edge, so the motion walks around it.
// softness rounds off the corners where the path meets the circle.
Effect<PVector> avoid(PVector center, float r, float soft) {
    return (PVector p, float t) -> {
        PVector away = PVector.sub(p, center);
        float d = away.mag();
        if (d >= r + soft || d == 0) return p;
        // A smooth max(d, r): the distance never drops below r,
        // and blends in over the softness band around the edge.
        float h = max(soft - abs(d - r), 0) / soft;
        float target = max(d, r) + h * h * soft / 4;
        return PVector.add(center, away.setMag(target));
    };
}

// Random points, wrapped around like in SplinePath to close the loop.
List<PVector> createLoop(int n) {
    float margin = 50;
    List<PVector> pts = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        pts.add(new PVector(random(margin, width - margin), random(margin, height - margin)));
    }
    List<PVector> wrapped = new ArrayList<>();
    wrapped.add(pts.get(n - 1));
    wrapped.addAll(pts);
    wrapped.add(pts.get(0));
    wrapped.add(pts.get(1));
    return wrapped;
}

void mousePressed() {
    loop = createLoop(points);
    spline.setPoints(loop);
}

void draw() {
    background(255);

    // The path without the effect. curveVertex() draws the same curve as
    // Spline, so the points can be passed straight in.
    noFill();
    stroke(220);
    strokeWeight(2);
    beginShape();
    for (PVector p : loop) {
        curveVertex(p.x, p.y);
    }
    endShape();

    stroke(255, 0, 0);
    circle(obstacle.x, obstacle.y, radius * 2);

    // The motion with the effect, as a trail. curveVertex() skips the
    // first and last points, so they are added twice.
    List<PVector> trail = pos.echo(200, 0.003f);
    PVector first = trail.get(0);
    PVector last = trail.get(trail.size() - 1);
    stroke(0);
    strokeWeight(5);
    beginShape();
    curveVertex(first.x, first.y);
    for (PVector p : trail) {
        curveVertex(p.x, p.y);
    }
    curveVertex(last.x, last.y);
    endShape();

    noStroke();
    fill(150);
    text("click for a new path", 40, 380);
}
