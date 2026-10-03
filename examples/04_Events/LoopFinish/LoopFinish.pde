import ch.domizai.keyed.*;

Timeline once;
Keyed<Float> x, progress;

int loops = 0;
int finishes = 0;
float doneFlash = 0;

void settings() {
    size(400, 400);
}

void setup() {
    Timeline tm = Keyed.init(this).setDuration(1);
    textFont(createFont("Courier", 14));

    x = Keyed.ofFloat()
        .key(Key.at(0).setEasing(1 / 3f), 60f)
        .key(Key.at(0.5f).setEasing(1 / 3f), 340f)
        .key(Key.at(1).setEasing(1 / 3f), 60f);

    // Called every time a looping timeline wraps around.
    // To stop it later, keep the lambda in a variable and pass it to removeListener().
    tm.onLoop(t -> loops++);

    // Plays once, in 2 seconds.
    once = new Timeline().setDuration(2, false);
    progress = Keyed.ofFloat()
        .setTimeline(once)
        .key(0, 0f)
        .key(2, 1f);

    // Called once when a non-looping timeline reaches its end.
    once.onFinish(t -> {
        finishes++;
        doneFlash = 255;
    });
}

void draw() {
    background(255);
    noStroke();

    fill(150);
    text("onLoop    loops: " + loops, 40, 70);
    fill(0);
    circle(x.value(), 120, 30);

    fill(150);
    text("onFinish  finishes: " + finishes, 40, 230);
    fill(235);
    rect(40, 260, 320, 20);
    fill(0);
    rect(40, 260, 320 * progress.value(), 20);

    fill(230, 60, 60, doneFlash);
    text("done!", 40, 310);
    doneFlash *= 0.95f;

    fill(150);
    text("click to restart the bar", 40, 370);
}

void mousePressed() {
    // After restarting, onFinish fires again at the end.
    once.to(0);
}
