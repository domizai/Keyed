import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;

PVector[] corners = {
    new PVector(100, 80),
    new PVector(300, 80),
    new PVector(300, 280),
    new PVector(100, 280)
};

Keyed<PVector> plain, shortLag, longLag;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(4);
    textFont(createFont("Courier", 14));

    plain = square();

    // Lag averages the value over the last duration seconds: it trails
    // behind and rounds off sharp corners, without overshooting.
    // More samples is smoother, but each one costs an evaluation.
    shortLag = square().addEffect(Effect.lag(0.3f, 10));
    longLag = square().addEffect(Effect.lag(1, 30));

    // Effect.lag() is the shortcut for PVectors;
    // other types use new Lag<>(lerp, duration, samples).
}

// One corner per second, at constant speed.
Keyed<PVector> square() {
    Keyed<PVector> k = Keyed.ofPVector();
    for (int i = 0; i <= corners.length; i++) {
        k.key(i, corners[i % corners.length]);
    }
    return k;
}

void draw() {
    background(255);

    noFill();
    stroke(230);
    rect(100, 80, 200, 200);

    noStroke();
    PVector p = plain.value();
    fill(200);
    circle(p.x, p.y, 12);

    PVector a = shortLag.value();
    fill(0);
    circle(a.x, a.y, 24);

    PVector b = longLag.value();
    fill(230, 60, 60);
    circle(b.x, b.y, 24);

    fill(200);
    text("no effect", 40, 340);
    fill(0);
    text("Effect.lag(0.3f, 10)", 40, 360);
    fill(230, 60, 60);
    text("Effect.lag(1, 30)", 40, 380);
}
