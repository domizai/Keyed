import ch.domizai.keyed.*;

Timeline tm;
Pin hit;
Keyed<Float> x;

float leftFlash, rightFlash;
String last = "";

float barX0 = 40, barX1 = 360, barY = 300;

void settings() {
    size(400, 400);
}

void setup() {
    tm = Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));
    textAlign(CENTER, CENTER);

    hit = Pin.at(1.5f);

    x = Keyed.of(0f)
        .key(0, 50f)
        .key(hit, 350f)
        .key(3, 50f);

    // A marker calls back whenever playback crosses its time.
    tm.addMarker("left", 0, this::fired);
    // On the key's pin, it fires exactly when the ball arrives,
    // even after the pin is moved.
    tm.addMarker("right", hit, this::fired);
}

void fired(Marker m) {
    if (m.name().equals("left")) {
        leftFlash = 255;
    } else {
        rightFlash = 255;
    }
    last = m.name() + " at " + nf(m.t(), 1, 2);
}

void draw() {
    background(255);

    if (mousePressed) {
        // to(t, true) fires the markers passed on the way, in either direction.
        // A plain to(t) jumps silently.
        float t = map(constrain(mouseX, barX0, barX1), barX0, barX1, 0, tm.duration());
        tm.to(t, true);
    }

    // Walls that flash when their marker fires.
    noStroke();
    fill(230, 60, 60, leftFlash);
    rect(20, 60, 10, 160);
    fill(230, 60, 60, rightFlash);
    rect(370, 60, 10, 160);
    leftFlash *= 0.9f;
    rightFlash *= 0.9f;

    fill(0);
    circle(x.value(), 140, 40);

    // The bar, with every marker and the playhead.
    stroke(220);
    line(barX0, barY, barX1, barY);
    for (Marker m : tm.markers()) {
        float mx = map(m.t(), 0, tm.duration(), barX0, barX1);
        noStroke();
        fill(230, 60, 60);
        triangle(mx - 6, barY - 16, mx + 6, barY - 16, mx, barY - 6);
        fill(150);
        text(m.name(), mx, barY - 30);
    }
    stroke(0);
    float head = map(tm.t(), 0, tm.duration(), barX0, barX1);
    line(head, barY - 10, head, barY + 10);

    noStroke();
    fill(0);
    text(last, width / 2, 250);
    fill(150);
    text("drag to scrub   M toggle right", width / 2, barY + 40);
}

void mousePressed() {
    tm.play(false);
}

void mouseReleased() {
    tm.play(true);
}

void keyPressed() {
    if (key == 'm' || key == 'M') {
        // Markers can be looked up by name, removed and added again.
        Marker m = tm.marker("right");
        if (m != null) {
            tm.removeMarker(m);
        } else {
            tm.addMarker("right", hit, this::fired);
        }
    }
}
