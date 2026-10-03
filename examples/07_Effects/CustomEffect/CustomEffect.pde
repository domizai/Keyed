import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;

Keyed<PVector> plain, bobbing, delayed;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));

    plain = sweep(90);

    // An Effect gets the value and the time, and returns a new value.
    // This one bobs up and down twice per second.
    Effect<PVector> bob = (p, t) -> new PVector(p.x, p.y + 15 * sin(t * TWO_PI * 2));
    bobbing = sweep(210).addEffect(bob);

    // A TimeEffect gets the animation itself as a Tween,
    // so it can read the value at any other time.
    // This one plays the animation half a second late.
    TimeEffect<PVector> late = (source, t) -> source.value(t - 0.5f);
    delayed = sweep(330).addEffect(late);

    // Declaring the type tells Java which kind of effect the lambda is.
    // Both can also be classes that implement Effect or TimeEffect.
}

Keyed<PVector> sweep(float y) {
    return Keyed.ofPVector()
        .key(Key.at(0).setEasing(1 / 3f), new PVector(80, y))
        .key(Key.at(1.5f).setEasing(1 / 3f), new PVector(320, y))
        .key(Key.at(3).setEasing(1 / 3f), new PVector(80, y));
}

void draw() {
    background(255);
    lane("no effect", 90, plain);
    lane("Effect      (p, t) -> ...", 210, bobbing);
    lane("TimeEffect  (source, t) -> ...", 330, delayed);
}

void lane(String label, float y, Keyed<PVector> ball) {
    noStroke();
    fill(150);
    text(label, 40, y - 40);

    stroke(220);
    line(80, y, 320, y);

    PVector p = ball.value();
    noStroke();
    fill(0);
    circle(p.x, p.y, 24);
}
