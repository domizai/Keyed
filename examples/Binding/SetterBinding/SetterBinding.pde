import ch.domizai.keyed.*;
import ch.domizai.keyed.lerps.*;

int bg;
String label = "";
Keyed<Integer> score;
boolean bound = true;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));
    textAlign(CENTER, CENTER);

    // A Lerp, a default value and a setter, which receives
    // the animated value before every draw().
    Keyed.bind(new ColorLerp(), color(255), c -> bg = c)
        .key(0, color(255, 235, 215))
        .key(2, color(215, 230, 255))
        .key(4, color(255, 235, 215));

    // Or bind an existing Keyed. The setter can convert the value,
    // here from an int into a line of text.
    score = Keyed.ofInt(0)
        .key(0, 0)
        .key(4, 100)
        .bind(this::showScore);

    // apply() writes the value right away, without waiting for draw(),
    // e.g. to use label in setup().
    score.apply();
    println(label);
}

void showScore(int v) {
    label = "score " + v;
}

void draw() {
    // No value() calls: bg and label are already up to date.
    background(bg);

    fill(0);
    textSize(32);
    text(label, width / 2, 180);

    fill(150);
    textSize(14);
    text(bound ? "click to unbind" : "unbound, click to bind", width / 2, 350);
}

void mousePressed() {
    // unbind() stops the updates, so label keeps its last value.
    // bind() starts them again.
    bound = !bound;
    if (bound) {
        score.bind(this::showScore);
    } else {
        score.unbind();
    }
}
