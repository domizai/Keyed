import java.util.ArrayList;

import ch.domizai.keyed.*;

float x0 = 100, x1 = 340;

// There are IN, OUT and IN_OUT presets for QUAD, CUBIC, QUART and QUINT,
// plus SMOOTHSTEP and HOLD.
String[] names = { 
    "CUBIC_IN", 
    "CUBIC_OUT", 
    "CUBIC_IN_OUT",
    "QUAD_IN_OUT",
    "QUINT_IN_OUT",
    "SMOOTHSTEP"
};

Easing[] easings = {
    Easing.CUBIC_IN,
    Easing.CUBIC_OUT,
    Easing.CUBIC_IN_OUT,
    Easing.QUAD_IN_OUT,
    Easing.QUINT_IN_OUT,
    Easing.SMOOTHSTEP
};

ArrayList<Keyed<Float>> lanes = new ArrayList<>();

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));
    textAlign(LEFT, CENTER);

    for (Easing e : easings) {
        // An Easing shapes the whole segment from its key to the next one,
        // so the last key doesn't need one.
        lanes.add(Keyed.ofFloat()
            .key(Key.at(0).setEasing(e), x0)
            .key(Key.at(1.5f).setEasing(e), x1)
            .key(3, x0));
    }
}

void draw() {
    background(255);
    for (int i = 0; i < lanes.size(); i++) {
        float y = 60 + i * 56;
        graph(easings[i], 30, y);
        lane(names[i], y, lanes.get(i));
    }
}

// An Easing is a function from linear progress to eased progress, both 0..1.
void graph(Easing e, float x, float y) {
    float s = 36;
    noFill();
    stroke(220);
    rect(x, y - s / 2, s, s);
    stroke(0);
    beginShape();
    for (int i = 0; i <= 40; i++) {
        float d = i / 40f;
        vertex(x + d * s, y + s / 2 - e.apply(d) * s);
    }
    endShape();
}

void lane(String label, float y, Keyed<Float> x) {
    noStroke();
    fill(150);
    text(label, x0, y - 20);

    stroke(220);
    line(x0, y, x1, y);

    noStroke();
    fill(0);
    circle(x.value(), y, 16);
}
