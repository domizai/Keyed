import java.util.ArrayList;

import ch.domizai.keyed.*;
import ch.domizai.keyed.lerps.*;

ArrayList<Ball> balls = new ArrayList<>();

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(2);

    for (int i = 0; i < 7; i++) {
        Ball b = new Ball(this, 60 + i * 47, 300);
        balls.add(b);

        // Each ball starts a little later than the one before.
        float delay = i * 0.1f;

        // Binds the float field y of this ball, by name.
        Keyed.bind(b, "y")
            .key(Key.at(delay).setEasing(1 / 3f), 300f)
            .key(Key.at(delay + 0.5f).setEasing(1 / 3f), 120f)
            .key(Key.at(delay + 1).setEasing(1 / 3f), 300f);

        // Fields can only be bound if they are floats. For anything else,
        // like this int color, bind a setter of the object instead.
        Keyed.bind(new ColorLerp(), color(0), b::setColor)
            .key(delay, color(0))
            .key(delay + 0.5f, color(230, 60, 60))
            .key(delay + 1, color(0));
    }
}

void draw() {
    background(255);
    // The balls are already up to date; they just draw themselves.
    for (Ball b : balls) {
        b.draw();
    }
}
