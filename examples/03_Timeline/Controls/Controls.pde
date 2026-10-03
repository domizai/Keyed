import ch.domizai.keyed.*;

Timeline tm;
Keyed<Float> x;

void settings() {
    size(400, 400);
}

void setup() {
    // Call init() first: timelines created after it advance with the sketch.
    Keyed.init(this);
    textFont(createFont("Courier", 14));

    // A timeline of our own, 3 seconds long and looping.
    tm = new Timeline().setDuration(3);

    // setTimeline() makes x follow tm instead of the default timeline.
    x = Keyed.of(0f)
        .setTimeline(tm)
        .key(Key.at(0).setEasing(1 / 3f), 60f)
        .key(Key.at(1.5f).setEasing(1 / 3f), 340f)
        .key(Key.at(3).setEasing(1 / 3f), 60f);
}

void draw() {
    background(255);

    noStroke();
    fill(0);
    circle(x.value(), 100, 40);

    // Progress bar with the playhead.
    stroke(220);
    line(40, 160, 360, 160);
    stroke(0);
    float head = map(tm.t(), 0, tm.duration(), 40, 360);
    line(head, 150, head, 170);

    fill(0);
    text("t         " + nf(tm.t(), 1, 2), 40, 210);
    text("playing   " + tm.isPlaying(), 40, 235);
    text("speed     " + nf(tm.speed(), 1, 1), 40, 260);
    text("looping   " + tm.isLooping(), 40, 285);
    text("finished  " + tm.isFinished(), 40, 310);

    fill(150);
    text("SPACE play/pause   R restart", 40, 350);
    text("LEFT/RIGHT speed   L loop", 40, 370);
}

void keyPressed() {
    if (key == ' ') {
        tm.play(!tm.isPlaying());
    } else if (key == 'r' || key == 'R') {
        // Jumps back to the start.
        tm.to(0);
    } else if (key == 'l' || key == 'L') {
        // Without looping the timeline stops at its duration.
        tm.loop(!tm.isLooping());
    } else if (keyCode == RIGHT) {
        tm.setSpeed(tm.speed() + 0.5f);
    } else if (keyCode == LEFT) {
        // Negative speeds play backwards.
        tm.setSpeed(tm.speed() - 0.5f);
    }
}
