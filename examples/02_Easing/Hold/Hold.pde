import ch.domizai.keyed.*;

int ticks = 8;
Keyed<Float> hand;
Keyed<Integer> lamp;
int[] lampColors;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));
    textAlign(CENTER, CENTER);

    // Even jumps: one full turn in 8 equal steps,
    // with a stepped easing on a single segment.
    hand = Keyed.ofFloat()
        .key(Key.at(0).setEasing(Easing.steps(ticks)), 0f)
        .key(4, TWO_PI);

    // Uneven timing: green for 2 seconds, yellow for 0.5, red for 1.5.
    // hold() keeps a key's value until the next key. Without it the lamp
    // index would be blended and rounded, switching halfway between keys.
    lamp = Keyed.ofInt()
        .key(Key.at(0).hold(), 2)
        .key(Key.at(2).hold(), 1)
        .key(2.5f, 0);

    // Red, yellow and green, from top to bottom.
    lampColors = new int[] {
        color(230, 60, 60),
        color(240, 190, 40),
        color(60, 180, 90)
    };
}

void draw() {
    background(255);
    clock("steps(8)", 110, 200, hand.value());
    trafficLight("hold()", 290, 200, lamp.value());
}

void clock(String label, float x, float y, float angle) {
    float r = 70;

    noFill();
    stroke(220);
    circle(x, y, r * 2);
    strokeWeight(5);
    for (int i = 0; i < ticks; i++) {
        float a = i * TWO_PI / ticks - HALF_PI;
        point(x + cos(a) * (r - 10), y + sin(a) * (r - 10));
    }

    stroke(0);
    strokeWeight(3);
    float a = angle - HALF_PI;
    line(x, y, x + cos(a) * (r - 20), y + sin(a) * (r - 20));
    strokeWeight(1);

    noStroke();
    fill(150);
    text(label, x, y + 120);
}

void trafficLight(String label, float x, float y, int lit) {
    noFill();
    stroke(220);
    rect(x - 35, y - 95, 70, 190, 12);

    noStroke();
    for (int i = 0; i < 3; i++) {
        fill(i == lit ? lampColors[i] : color(235));
        circle(x, y - 60 + i * 60, 44);
    }

    fill(150);
    text(label, x, y + 120);
}
