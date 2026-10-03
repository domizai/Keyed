import ch.domizai.keyed.*;

Keyed<Integer> col;
Keyed<Integer> count;
Keyed<String> word;
Keyed<Boolean> visible;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 18));
    textAlign(LEFT, CENTER);

    // Colors are ints in Processing, so they have their own factory.
    col = Keyed.ofColor(color(0))
        .key(0, color(230, 60, 60))
        .key(2, color(60, 120, 230))
        .key(4, color(230, 60, 60));

    // Whole numbers, rounded to the nearest int.
    count = Keyed.ofInt(0)
        .key(0, 0)
        .key(2, 100)
        .key(4, 0);

    // Text morphs one character edit at a time.
    word = Keyed.of("")
        .key(0, "keyed")
        .key(2, "animation")
        .key(4, "keyed");

    // Booleans can't blend, so they switch when the next key is reached.
    visible = Keyed.of(false)
        .key(0, true)
        .key(2, false);
}

void draw() {
    background(255);
    noStroke();

    label("ofColor", 80);
    fill(col.value());
    rect(180, 60, 160, 40);

    label("ofInt", 160);
    fill(0);
    text(count.value(), 180, 160);
    rect(230, 150, count.value() * 1.1f, 20);

    label("of(String)", 240);
    fill(0);
    text(word.value(), 180, 240);

    label("of(boolean)", 320);
    fill(visible.value() ? 0 : color(0, 0, 180));
    circle(200, 320, 40);
}

void label(String s, float y) {
    fill(150);
    text(s, 40, y);
}
