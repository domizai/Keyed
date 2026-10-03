import ch.domizai.keyed.*;

// Plain fields. Their values here become the defaults.
float x = 60;
float diameter = 20;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));

    // bind(object, "field") writes the animated value into a float field
    // before every draw(). It works for float fields of any object,
    // e.g. Keyed.bind(ball, "x").
    Keyed.bind(this, "x")
        .key(Key.at(0).setEasing(1 / 3f), 60f)
        .key(Key.at(1.5f).setEasing(1 / 3f), 340f)
        .key(Key.at(3).setEasing(1 / 3f), 60f);

    Keyed.bind(this, "diameter")
        .key(0, 20f)
        .key(1.5f, 80f)
        .key(3, 20f);
}

void draw() {
    background(255);

    // No value() calls: x and diameter are plain floats, already up to date.
    noStroke();
    fill(0);
    circle(x, 180, diameter);

    fill(150);
    text("x = " + nf(x, 1, 1), 40, 330);
    text("diameter = " + nf(diameter, 1, 1), 40, 355);
}
