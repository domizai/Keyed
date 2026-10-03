import java.util.ArrayList;

import ch.domizai.keyed.*;
import ch.domizai.keyed.types.*;

Keyed<ShapeMorph> shape;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));

    // ShapeMorph is a closed polygon that blends point by point.
    // The shape with fewer points gets extra points on its longest edges,
    // so outlines and corners stay sharp while it morphs.
    shape = Keyed.of(polygon(3, 130))
        .key(Key.at(0).setEasing(0.6f), polygon(3, 130))
        .key(Key.at(1).setEasing(0.6f), polygon(4, 120))
        .key(Key.at(2).setEasing(0.6f), star(5, 140, 60))
        .key(Key.at(3).setEasing(0.6f), polygon(40, 120))
        .key(Key.at(4).setEasing(0.6f), polygon(3, 130));
}

// All shapes start at the top, so they don't twist while morphing.
ShapeMorph polygon(int n, float r) {
    ArrayList<PVector> pts = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        float a = -HALF_PI + i * TWO_PI / n;
        pts.add(new PVector(200 + cos(a) * r, 190 + sin(a) * r));
    }
    return new ShapeMorph(pts);
}

ShapeMorph star(int n, float outer, float inner) {
    ArrayList<PVector> pts = new ArrayList<>();
    for (int i = 0; i < n * 2; i++) {
        float a = -HALF_PI + i * PI / n;
        float r = i % 2 == 0 ? outer : inner;
        pts.add(new PVector(200 + cos(a) * r, 190 + sin(a) * r));
    }
    return new ShapeMorph(pts);
}

void draw() {
    background(255);

    ShapeMorph s = shape.value();

    noStroke();
    fill(0);
    beginShape();
    s.vertices(this);
    endShape(CLOSE);

    // The points it is made of.
    fill(230, 60, 60);
    for (PVector p : s.points) {
        circle(p.x, p.y, 6);
    }

    fill(150);
    text(s.points.size() + " points", 40, 380);
}
