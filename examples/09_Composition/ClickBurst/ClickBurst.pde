import java.util.ArrayList;

import ch.domizai.keyed.*;

ArrayList<Burst> bursts = new ArrayList<>();
int[] colors;

void settings() {
    size(400, 400);
}

void setup() {
    textFont(createFont("Courier", 14));

    colors = new int[] {
        color(230, 60, 60),
        color(240, 190, 40),
        color(60, 120, 230),
        color(60, 180, 90)
    };

    // Starts a burst every 0.4 seconds, so there is something to see.
    Keyed.init(this)
        .setDuration(0.4f)
        .onLoop(tm -> spawn(random(60, 340), random(60, 320)));
}

void mousePressed() {
    spawn(mouseX, mouseY);
}

// The same Burst every time, but each one gets its own size, color and duration.
// Each one plays on its own, starting right away.
void spawn(float x, float y) {
    float size = random(0.4f, 1.6f);
    int col = colors[(int) random(colors.length)];
    // Bigger bursts take longer.
    float duration = 0.4f + 0.5f * size;
    bursts.add(new Burst(this, x, y, size, col, duration));
}

void draw() {
    background(255);

    // Backwards, so finished bursts can be removed while looping.
    for (int i = bursts.size() - 1; i >= 0; i--) {
        Burst b = bursts.get(i);
        b.draw();
        if (b.isFinished()) {
            // Stops its timeline so it can be garbage collected.
            b.dispose();
            bursts.remove(i);
        }
    }

    fill(150);
    text("click anywhere   bursts: " + bursts.size(), 40, 380);
}
