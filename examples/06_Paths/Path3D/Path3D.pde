import ch.domizai.keyed.*;
import ch.domizai.keyed.tween.*;

float ground = 60;
PVector[] pts;
Spline track;
Tween<PVector> shadowPath;
Keyed<PVector> ball, shadow;
Keyed<Float> spin;

void settings() {
    size(400, 400, P3D);
}

void setup() {
    Keyed.init(this).setDuration(6);
    textFont(createFont("Courier", 14));

    // Six points around a circle on the ground (x and z),
    // alternating high and low (y points down in Processing).
    int n = 6;
    pts = new PVector[n];
    for (int i = 0; i < n; i++) {
        float a = i * TWO_PI / n;
        float y = i % 2 == 0 ? -80 : 30;
        pts[i] = new PVector(cos(a) * 130, y, sin(a) * 130);
    }

    // Paths use x, y and z, so they work in 3D just like in 2D.
    // Wrapping the points around closes the loop, as in SplinePath.
    track = new Spline(pts[n - 1], pts[0], pts[1], pts[2], pts[3], pts[4], pts[5], pts[0], pts[1]);

    ball = Keyed.of(new PVector())
        .key(0, track)
        .key(6, track);

    // map() flattens the track onto the ground, for the shadow.
    shadowPath = track.map(p -> new PVector(p.x, ground, p.z));
    shadow = Keyed.of(new PVector())
        .key(0, shadowPath)
        .key(6, shadowPath);

    // Half a turn per loop, to see the depth.
    spin = Keyed.of(0f)
        .key(0, 0f)
        .key(6, PI);
}

void draw() {
    background(255);

    fill(150);
    text("Spline in P3D", 40, 380);

    lights();
    translate(width / 2, height / 2);
    rotateX(-0.45f);
    rotateY(spin.value());

    // The track's shadow on the ground.
    noFill();
    stroke(230);
    drawPath(shadowPath);

    // The track.
    stroke(0);
    strokeWeight(2);
    drawPath(track);
    strokeWeight(1);

    // Posts from the ground up to each point.
    stroke(220);
    for (PVector p : pts) {
        line(p.x, ground, p.z, p.x, p.y, p.z);
    }

    PVector s = shadow.value();
    PVector b = ball.value();
    stroke(230, 60, 60, 120);
    line(s.x, s.y, s.z, b.x, b.y, b.z);

    noStroke();
    fill(200);
    pushMatrix();
    translate(s.x, s.y, s.z);
    rotateX(HALF_PI);
    circle(0, 0, 20);
    popMatrix();

    fill(230, 60, 60);
    pushMatrix();
    translate(b.x, b.y, b.z);
    sphere(12);
    popMatrix();
}

void drawPath(Tween<PVector> path) {
    beginShape();
    for (int i = 0; i <= 200; i++) {
        PVector v = path.value(i / 200f);
        vertex(v.x, v.y, v.z);
    }
    endShape();
}
