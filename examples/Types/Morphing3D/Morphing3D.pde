import java.util.ArrayList;

import ch.domizai.keyed.*;
import ch.domizai.keyed.types.*;

int n = 60;
Keyed<ShapeMorph> shape;
Keyed<Float> spin;

void settings() {
    size(400, 400, P3D);
}

void setup() {
    Keyed.init(this).setDuration(8);
    textFont(createFont("Courier", 14));

    // ShapeMorph uses x, y and z, so outlines can morph in 3D too.
    shape = Keyed.of(circle())
        .key(Key.at(0).setEasing(0.6f), circle())
        .key(Key.at(2).setEasing(0.6f), wave())
        .key(Key.at(4).setEasing(0.6f), knot())
        .key(Key.at(6).setEasing(0.6f), triangle())
        .key(Key.at(8).setEasing(0.6f), circle());

    // One turn per loop, to see the depth.
    spin = Keyed.of(0f)
        .key(0, 0f)
        .key(8, TWO_PI);
}

// All shapes start at the top, so they don't twist while morphing.

// A flat circle.
ShapeMorph circle() {
    ArrayList<PVector> pts = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        float a = -HALF_PI + i * TWO_PI / n;
        pts.add(new PVector(cos(a) * 120, sin(a) * 120, 0));
    }
    return new ShapeMorph(pts);
}

// A circle that goes up and down three times.
ShapeMorph wave() {
    ArrayList<PVector> pts = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        float a = -HALF_PI + i * TWO_PI / n;
        pts.add(new PVector(cos(a) * 110, sin(a) * 110, sin(a * 3) * 50));
    }
    return new ShapeMorph(pts);
}

// A trefoil knot.
ShapeMorph knot() {
    ArrayList<PVector> pts = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        float t = i * TWO_PI / n;
        pts.add(new PVector(
            (sin(t) + 2 * sin(2 * t)) * 40,
            (cos(t) - 2 * cos(2 * t)) * 40,
            -sin(3 * t) * 40));
    }
    return new ShapeMorph(pts);
}

// Only 3 points: ShapeMorph adds the missing ones along its edges.
ShapeMorph triangle() {
    ArrayList<PVector> pts = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
        float a = -HALF_PI + i * TWO_PI / 3;
        pts.add(new PVector(cos(a) * 130, sin(a) * 130, 0));
    }
    return new ShapeMorph(pts);
}

void draw() {
    background(255);

    fill(150);
    text("ShapeMorph in P3D", 40, 380);

    translate(width / 2, height / 2 - 10);
    rotateX(-0.5f);
    rotateY(spin.value());

    ShapeMorph s = shape.value();

    // vertices() includes z with a 3D renderer.
    noFill();
    stroke(0);
    strokeWeight(1.5f);
    beginShape();
    s.vertices(this);
    endShape(CLOSE);

    stroke(230, 60, 60);
    strokeWeight(7);
    for (PVector p : s.points) {
        point(p.x, p.y, p.z);
    }
    strokeWeight(1);
}
