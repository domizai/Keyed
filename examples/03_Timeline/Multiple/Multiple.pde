import ch.domizai.keyed.*;

Timeline intro;
Keyed<Float> angle, cardY, cardAlpha;

void settings() {
    size(400, 400);
}

void setup() {
    // The default timeline loops every second.
    Keyed.init(this).setDuration(1);
    textFont(createFont("Courier", 14));
    textAlign(CENTER, CENTER);

    // Without setTimeline(), values follow the default timeline.
    angle = Keyed.ofFloat()
        .key(0, 0f)
        .key(1, TWO_PI);

    // A second timeline that plays once: false turns looping off,
    // so it stops at the end instead of starting over.
    intro = new Timeline().setDuration(1.2f, false);

    cardY = Keyed.ofFloat()
        .setTimeline(intro)
        .key(Key.at(0).setEasing(Easing.CUBIC_OUT), 420f)
        .key(1.2f, 180f);

    cardAlpha = Keyed.ofFloat()
        .setTimeline(intro)
        .key(0, 0f)
        .key(0.6f, 255f);
}

void draw() {
    background(255);

    // The spinner keeps turning, whatever happens to the intro.
    noFill();
    stroke(0);
    strokeWeight(4);
    float a = angle.value();
    arc(200, 70, 50, 50, a, a + PI * 1.5f);
    strokeWeight(1);

    fill(150);
    text("intro finished: " + intro.isFinished(), 200, 130);

    noStroke();
    fill(0, cardAlpha.value());
    rect(80, cardY.value(), 240, 140, 12);
    fill(255, cardAlpha.value());
    text("click to replay", 200, cardY.value() + 70);
}

void mousePressed() {
    // Restarts only the intro; the default timeline is unaffected.
    intro.to(0);
}
