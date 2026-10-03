import ch.domizai.keyed.*;

// An animated float: its value changes over time, blending from key to key.
Keyed<Float> x;

void settings() {
    size(400, 400);
}

void setup() {
    // Connects Keyed to the sketch so animations follow real time.
    // It returns the default timeline, which we loop every 2 seconds.
    Keyed.init(this).setDuration(2);

    // Each key is a time in seconds and a value.
    x = Keyed.of(0f)
        .key(0, 50f)
        .key(1, 350f)
        .key(2, 50f);
}

void draw() {
    background(255);
    noStroke();
    fill(0);
    // value() is the value at the current time.
    circle(x.value(), height / 2, 40);
}
