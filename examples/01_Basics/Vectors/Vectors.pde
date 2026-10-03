import ch.domizai.keyed.*;

// An animated PVector blends x, y and z together.
Keyed<PVector> pos;

PVector[] corners = {
    new PVector(100, 100),
    new PVector(300, 100),
    new PVector(300, 300),
    new PVector(100, 300)
};

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);

    pos = Keyed.ofPVector();
    // One corner per second. The last key returns to the first corner,
    // so the loop closes without a jump.
    for (int i = 0; i <= corners.length; i++) {
        pos.key(i, corners[i % corners.length]);
    }
    // Keys store a copy, so changing corners now would not move them.
}

void draw() {
    background(255);
    noStroke();

    fill(200);
    for (PVector c : corners) {
        circle(c.x, c.y, 10);
    }

    PVector p = pos.value();
    fill(0);
    circle(p.x, p.y, 40);
}
