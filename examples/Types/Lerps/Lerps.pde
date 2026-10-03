import ch.domizai.keyed.*;
import ch.domizai.keyed.lerps.*;

Keyed<Float> longWay, shortWay;
Keyed<Integer> rgb, hsb;
Keyed<String> morph, step;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 24));
    textSize(14);
    textAlign(CENTER, CENTER);

    // A Lerp decides how two values blend. Keyed.of() picks one for you,
    // new Keyed<>(lerp, default) lets you choose.

    // Keyed.of(float) uses FloatLerp, which blends the numbers:
    // from 330° to 30° it turns back 300°.
    longWay = Keyed.of(0f)
        .key(Key.at(0).setEasing(1 / 3f), radians(330))
        .key(Key.at(2).setEasing(1 / 3f), radians(30))
        .key(Key.at(4).setEasing(1 / 3f), radians(330));

    // AngleLerp takes the short way round: 60° forward, across 0°.
    shortWay = new Keyed<>(new AngleLerp(), 0f)
        .key(Key.at(0).setEasing(1 / 3f), radians(330))
        .key(Key.at(2).setEasing(1 / 3f), radians(30))
        .key(Key.at(4).setEasing(1 / 3f), radians(330));

    // Keyed.ofColor() uses ColorLerp, which blends red, green and blue:
    // red to cyan passes through gray.
    rgb = Keyed.ofColor(0)
        .key(0, color(255, 0, 0))
        .key(2, color(0, 255, 255))
        .key(4, color(255, 0, 0));

    // Lerp is a functional interface, so a lambda works too.
    // This one blends in HSB, passing through the hues instead.
    Lerp<Integer> hsbLerp = (a, b, d) -> lerpColor(a, b, d, HSB);
    hsb = new Keyed<>(hsbLerp, 0)
        .key(0, color(255, 0, 0))
        .key(2, color(0, 255, 255))
        .key(4, color(255, 0, 0));

    // Keyed.of(String) uses StringLerp, which morphs one character at a time.
    morph = Keyed.of("")
        .key(0, "keyed")
        .key(2, "lerps")
        .key(4, "keyed");

    // StepLerp doesn't blend: it switches once the blend passes a threshold,
    // here halfway between keys. It works for any type.
    step = new Keyed<>(new StepLerp<String>(0.5f), "")
        .key(0, "keyed")
        .key(2, "lerps")
        .key(4, "keyed");
}

void draw() {
    background(255);

    dial("FloatLerp", 110, 90, longWay.value());
    dial("AngleLerp", 290, 90, shortWay.value());

    noStroke();
    fill(rgb.value());
    rect(40, 190, 140, 40);
    fill(hsb.value());
    rect(220, 190, 140, 40);
    fill(150);
    text("ColorLerp", 110, 250);
    text("lambda, HSB", 290, 250);

    fill(0);
    textSize(24);
    text(morph.value(), 110, 300);
    text(step.value(), 290, 300);
    fill(150);
    textSize(14);
    text("StringLerp", 110, 330);
    text("StepLerp(0.5f)", 290, 330);
}

void dial(String label, float x, float y, float angle) {
    noFill();
    stroke(220);
    circle(x, y, 100);

    // 0° points up.
    stroke(0);
    strokeWeight(3);
    line(x, y, x + sin(angle) * 40, y - cos(angle) * 40);
    strokeWeight(1);

    noStroke();
    fill(150);
    float deg = (degrees(angle) % 360 + 360) % 360;
    text(label + "  " + round(deg) + "°", x, y + 70);
}
