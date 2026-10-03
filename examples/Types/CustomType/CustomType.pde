import ch.domizai.keyed.*;

Keyed<Transform> transform;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));
    rectMode(CENTER);

    // Transform (see its tab) implements Lerpable: it knows how to blend
    // itself, so Keyed.of() needs no Lerp. Any class can do this.
    // Position, rotation and scale are animated together, in one value.
    transform = Keyed.of(new Transform(200, 200))
        .key(Key.at(0).setEasing(1 / 3f), new Transform(100, 120, 0, 1))
        .key(Key.at(1.3f).setEasing(1 / 3f), new Transform(300, 120, radians(120), 1.6f))
        .key(Key.at(2.6f).setEasing(1 / 3f), new Transform(200, 290, radians(240), 0.6f))
        // Transform.lerp() turns the short way: from 240° to 0° it turns
        // forward 120°, not back 240°.
        .key(Key.at(4).setEasing(1 / 3f), new Transform(100, 120, 0, 1));
}

void draw() {
    background(255);

    pushMatrix();

    Transform t = transform.value();
    translate(t.x, t.y);
    rotate(t.rotation);
    scale(t.scale);
    // or simply:
    // transform.value().apply(this);

    noStroke();
    fill(0);
    rect(0, 0, 60, 60);
    // A notch, to see the rotation.
    fill(255);
    circle(0, -20, 10);
    popMatrix();

    fill(150);
    text("Keyed.of(new Transform(...))", 40, 380);
}
