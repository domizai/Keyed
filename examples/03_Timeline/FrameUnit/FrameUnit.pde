import ch.domizai.keyed.*;

int fps = 30;
Timeline synced, stepped;
Keyed<Float> a, b;

void settings() {
    size(400, 400);
}

void setup() {
    frameRate(fps);
    textFont(createFont("Courier", 14));

    // Keyed can't read the sketch's frame rate, so pass the same value.
    Keyed.setFrameRate(fps);
    // Count time in frames instead of seconds, for every timeline created afterwards.
    Keyed.setUnit(Keyed.FRAME);
    Keyed.init(this);

    // Follows the real clock, measured in frames.
    // If draw() is slow it skips ahead to stay on time.
    synced = new Timeline().setDuration(90);

    // Advances exactly one frame per draw().
    // If draw() is slow it slows down, but never skips a frame.
    stepped = new Timeline().setDuration(90).sync(false);

    a = bounce(synced);
    b = bounce(stepped);
}

// Keys at frames 0, 45 and 90: 3 seconds at 30 fps.
Keyed<Float> bounce(Timeline tm) {
    return Keyed.ofFloat()
        .setTimeline(tm)
        .key(Key.at(0).setEasing(1 / 3f), 60f)
        .key(Key.at(45).setEasing(1 / 3f), 340f)
        .key(Key.at(90).setEasing(1 / 3f), 60f);
}

void draw() {
    // Simulates a heavy sketch while the mouse is held down.
    if (mousePressed) {
        delay(100);
    }
    background(255);

    lane("sync(true)", synced, a, 110);
    lane("sync(false)", stepped, b, 240);

    fill(150);
    text("hold the mouse to slow down draw()", 40, 360);
}

void lane(String label, Timeline tm, Keyed<Float> x, float y) {
    fill(150);
    text(label, 40, y - 40);
    fill(0);
    text("frame " + nf(tm.t(), 2, 1), 240, y - 40);

    stroke(220);
    line(60, y, 340, y);
    noStroke();
    fill(0);
    circle(x.value(), y, 30);
}
