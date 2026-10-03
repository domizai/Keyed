import ch.domizai.keyed.*;

Timeline tm;
Keyed<PVector> pos;

float barX0 = 40, barX1 = 360, barY = 340;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this);
    textFont(createFont("Courier", 14));
    textAlign(CENTER, CENTER);

    tm = new Timeline().setDuration(4);

    pos = Keyed.ofPVector()
        .setTimeline(tm)
        .key(Key.at(0).setEasing(1 / 3f), new PVector(80, 80))
        .key(Key.at(1).setEasing(1 / 3f), new PVector(320, 80))
        .key(Key.at(2.5f).setEasing(1 / 3f), new PVector(320, 260))
        .key(Key.at(4).setEasing(1 / 3f), new PVector(80, 80));
}

void draw() {
    background(255);

    if (mousePressed) {
        // to() jumps straight to a time.
        float t = map(constrain(mouseX, barX0, barX1), barX0, barX1, 0, tm.duration());
        tm.to(t);
    }

    PVector p = pos.value();
    noStroke();
    fill(0);
    circle(p.x, p.y, 40);

    // The bar, with a tick for every key.
    stroke(220);
    line(barX0, barY, barX1, barY);
    for (Key k : pos.keys()) {
        float kx = map(k.t(), 0, tm.duration(), barX0, barX1);
        line(kx, barY - 6, kx, barY + 6);
    }

    // The playhead.
    stroke(0);
    float head = map(tm.t(), 0, tm.duration(), barX0, barX1);
    line(head, barY - 12, head, barY + 12);

    noStroke();
    fill(150);
    text("drag to scrub", width / 2, barY + 30);
}

// Pause while dragging, so the timeline doesn't advance on its own.
void mousePressed() {
    tm.play(false);
}

void mouseReleased() {
    tm.play(true);
}
