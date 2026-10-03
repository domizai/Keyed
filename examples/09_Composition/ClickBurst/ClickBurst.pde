import java.util.ArrayList;

import ch.domizai.keyed.*;

ArrayList<Burst> bursts = new ArrayList<>();

void settings() {
    size(400, 400);
}

void setup() {
    textFont(createFont("Courier", 14));

    // Starts a burst every 0.6 seconds, so there is something to see.
    Keyed.init(this)
        .setDuration(0.6f)
        .onLoop(tm -> bursts.add(new Burst(this, random(60, 340), random(60, 320))));
}

void mousePressed() {
    // Each Burst plays on its own, starting right away.
    bursts.add(new Burst(this, mouseX, mouseY));
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
