import ch.domizai.keyed.*;
import ch.domizai.keyed.tween.*;

float ground = 300;
float r = 15;

Keyed<PVector> ball;
Keyed<Float> shadowX, shadowWidth, squash;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(2);
    textFont(createFont("Courier", 14));

    // An arc, then the same arc backwards: a hop there and back.
    QuadraticBezier arc = new QuadraticBezier(
        new PVector(80, ground - r), new PVector(200, 0), new PVector(320, ground - r));
    Curve hop = arc.add(arc.slice(1, 0));

    ball = Keyed.of(new PVector())
        .key(0, hop)
        .key(2, hop);

    // x() turns a path into a Tween<Float> of its x coordinate (y() works too),
    // so the shadow follows the ball along the ground.
    Tween<Float> hopX = hop.x();
    shadowX = Keyed.of(0f)
        .key(0, hopX)
        .key(2, hopX);

    // map() turns a tween's values into something else:
    // the higher the ball, the narrower its shadow.
    Tween<Float> hopWidth = hop.map(p -> map(p.y, ground - r, 140, 60, 20));
    shadowWidth = Keyed.of(0f)
        .key(0, hopWidth)
        .key(2, hopWidth);

    // A Tween is just a function of d from 0 to 1, so a lambda works too.
    // This one squashes the ball as it lands, at d = 0, 0.5 and 1.
    Tween<Float> landing = d -> 1 - 0.3f * pow(abs(cos(d * TWO_PI)), 12);
    squash = Keyed.of(1f)
        .key(0, landing)
        .key(2, landing);
}

void draw() {
    background(255);

    stroke(220);
    line(40, ground, 360, ground);

    noStroke();
    fill(220);
    ellipse(shadowX.value(), ground, shadowWidth.value(), 8);

    // Squashed flat and wide, with the bottom kept on the path.
    PVector p = ball.value();
    float s = squash.value();
    fill(0);
    ellipse(p.x, p.y + r * (1 - s), 2 * r * (2 - s), 2 * r * s);

    fill(150);
    text("x()     shadow position", 40, 340);
    text("map()   shadow width", 40, 360);
    text("d -> .. squash on landing", 40, 380);
}
