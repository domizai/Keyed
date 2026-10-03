import java.util.ArrayList;

import ch.domizai.keyed.*;

float x0 = 100, x1 = 340;

String[] names = {
    "cubicBezier(0.25, 0.1, 0.25, 1)",
    "cubicBezier(0.34, 1.56, 0.64, 1)",
    "cubicBezier(0.36, 0, 0.66, -0.56)",
    "quadraticBezier(0, 1.4, 1)",
    "smoothstep(0.3, 0.7)",
    "steps(4)",
    "d -> ... (bounce)"
};
Easing[] easings;

ArrayList<Keyed<Float>> lanes = new ArrayList<>();

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));
    textAlign(LEFT, CENTER);

    easings = new Easing[] {
        // Same as CSS cubic-bezier(); this one is CSS "ease".
        Easing.cubicBezier(0.25f, 0.1f, 0.25f, 1),
        // y values above 1 overshoot the target...
        Easing.cubicBezier(0.34f, 1.56f, 0.64f, 1),
        // ...and below 0 pull back before moving.
        Easing.cubicBezier(0.36f, 0, 0.66f, -0.56f),
        // Starts at 0, ends at 1, pulled towards the middle value.
        Easing.quadraticBezier(0, 1.4f, 1),
        // Waits until 0.3, moves smoothly, arrives at 0.7.
        Easing.smoothstep(0.3f, 0.7f),
        // Jumps in 4 equal steps, like CSS steps().
        Easing.steps(4),
        // Easing is a functional interface, so any function of d works.
        // This one hits the target, bounces back twice and settles.
        d -> 1 - abs(cos(d * 2.5f * PI)) * (1 - d)
    };

    for (Easing e : easings) {
        lanes.add(Keyed.ofFloat()
            .key(Key.at(0).setEasing(e), x0)
            .key(Key.at(1.5f).setEasing(e), x1)
            .key(3, x0));
    }
}

void draw() {
    background(255);
    for (int i = 0; i < lanes.size(); i++) {
        float y = 45 + i * 52;
        graph(easings[i], 30, y);
        lane(names[i], y, lanes.get(i));
    }
}

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
