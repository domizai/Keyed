import java.util.ArrayList;
import java.util.List;

import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.lerps.*;
import ch.domizai.keyed.tween.*;
import ch.domizai.keyed.types.*;

/**
 * A 12 second loop in four scenes. It starts flat like a 2D sketch, and the
 * first impact tilts the camera to reveal that everything was 3D all along.
 *
 * Every scene starts on a Pin. Keys, markers and the HUD all hang off them,
 * so moving a pin retimes the whole piece.
 *
 * Drag on the timeline to scrub, SPACE to pause.
 */
static final float D = 12;
static final int N = 120;

String[] names = { "keyed", "morph", "spring", "orbit" };
int[] palette;
int bg;

Timeline tm;
Pin[] scene;

Keyed<ShapeMorph> core;
Keyed<Float> rippleAmp, spin;
Keyed<Quaternion> cam;
Keyed<Integer> accent;
Keyed<String> word;
Keyed<PVector> comet;
Keyed<Float> zoomKeyed;
List<Keyed<PVector>> moons = new ArrayList<>();
List<Shockwave> waves = new ArrayList<>();
PVector[] dust;

// Bound below: Keyed writes it before every draw().
float zoom = 1;

float barX0 = 40, barX1 = 600, barY = 604;
boolean scrubbing = false;

void settings() {
    size(640, 640, P3D);
    smooth(8);
}

void setup() {
    tm = Keyed.init(this).setDuration(D);
    textFont(createFont("Courier", 40));

    bg = color(10, 10, 16);
    palette = new int[] {
        color(255, 70, 80),
        color(255, 190, 50),
        color(70, 140, 255),
        color(60, 230, 160)
    };

    scene = new Pin[] { Pin.at(0), Pin.at(3), Pin.at(6), Pin.at(9) };

    // The hero: a closed outline that snaps to a new shape on every scene
    // and wobbles into it like jelly. Spring works on any type with a Lerp,
    // ShapeMorph included, and wraps around the loop seamlessly.
    ShapeMorph[] shapes = { circle(150), knot(52), star(5, 190, 75), flower() };
    core = Keyed.of(shapes[0]);
    for (int i = 0; i < 4; i++) {
        core.key(Key.at(scene[i]).hold(), shapes[i]);
    }
    core.key(D, shapes[0]);

    // A custom effect runs a wave through the outline's depth.
    // Looking straight on you can't see it; the camera reveals it.
    Effect<ShapeMorph> ripple = this::rippled;
    core.addEffect(new Spring<>(ShapeMorph::lerp, 1.4f, 0.3f))
        .addEffect(ripple);

    rippleAmp = Keyed.ofFloat()
        .key(Key.at(scene[0]).setEasing(0.6f), 40f)
        .key(Key.at(scene[1]).setEasing(0.6f), 10f)
        .key(Key.at(scene[2]).setEasing(0.6f), 55f)
        .key(Key.at(scene[3]).setEasing(0.6f), 25f)
        .key(Key.at(D).setEasing(0.6f), 40f);

    // The camera: a quaternion per scene, sprung like a heavy rig, so
    // every cut whips around and settles. It starts dead flat.
    Quaternion[] views = {
        Quaternion.identity(),
        euler(1.05f, 0, 0.35f),
        euler(0.5f, 0.7f, 0),
        euler(1.3f, 0, -0.5f)
    };
    cam = Keyed.of(views[0]);
    for (int i = 0; i < 4; i++) {
        cam.key(Key.at(scene[i]).hold(), views[i]);
    }
    cam.key(D, views[0])
        .addEffect(new Spring<>(Quaternion::lerp, 0.9f, 0.55f));

    // A slow turntable underneath the camera.
    spin = Keyed.ofFloat()
        .key(0, 0f)
        .key(D, -TWO_PI);

    // Field binding: zoom is a plain float, kept up to date by Keyed.
    float[] zooms = { 1, 1.15f, 0.92f, 1.08f };
    zoomKeyed = Keyed.bind(this, "zoom");
    for (int i = 0; i < 4; i++) {
        zoomKeyed.key(Key.at(scene[i]).hold(), zooms[i]);
    }
    zoomKeyed.key(D, zooms[0])
        .addEffect(new Spring<>(new FloatLerp(), 2.2f, 0.25f));

    // Lag crossfades the hard color cuts...
    accent = Keyed.ofColor();
    for (int i = 0; i < 4; i++) {
        accent.key(Key.at(scene[i]).hold(), palette[i]);
    }
    accent.key(D, palette[0])
        .addEffect(new Lag<>(new ColorLerp(), 0.6f, 16));

    // ...and, being generic, morphs the words letter by letter.
    word = Keyed.ofString();
    for (int i = 0; i < 4; i++) {
        word.key(Key.at(scene[i]).hold(), names[i]);
    }
    word.key(D, names[0])
        .addEffect(new Lag<>(new StringLerp(), 0.5f, 10));

    // Six moons: each holds a formation per scene, springs into the next
    // one with its own stiffness, and circles on top of that. Effects stack.
    for (int i = 0; i < 6; i++) {
        float a = i * TWO_PI / 6 - HALF_PI;
        float side = i % 2 == 0 ? 1 : -1;
        PVector[] formation = {
            new PVector(cos(a) * 215, sin(a) * 215, 0),
            new PVector(cos(a) * 110, sin(a) * 110, side * 150),
            new PVector(0, 0, 50 + i * 42),
            new PVector(cos(a + PI / 6) * 240, sin(a + PI / 6) * 240, side * 70)
        };
        Keyed<PVector> m = Keyed.ofPVector();
        for (int s = 0; s < 4; s++) {
            m.key(Key.at(scene[s]).hold(), formation[s]);
        }
        m.key(D, formation[0])
            .addEffect(Effect.spring(1.3f + i * 0.2f, 0.35f))
            // Whole number of turns per loop, so the loop stays seamless.
            .addEffect(new Orbit(16 + i * 3, 0.5f + 0.25f * (i % 4), new PVector(cos(a), sin(a), 1.5f)));
        moons.add(m);
    }

    // A comet on a closed 3D spline, two laps per loop, with a bit of wiggle.
    int n = 7;
    PVector[] pts = new PVector[n];
    for (int i = 0; i < n; i++) {
        float a = i * TWO_PI / n;
        float r = 280 + 40 * sin(3 * a);
        pts[i] = new PVector(cos(a) * r, sin(a) * r, 130 * sin(2 * a + 1));
    }
    Spline lap = new Spline(pts[n - 1], pts[0], pts[1], pts[2], pts[3], pts[4], pts[5], pts[6], pts[0], pts[1]);
    Curve twoLaps = new Curve(lap, lap);
    comet = Keyed.ofPVector()
        .key(0, twoLaps)
        .key(D, twoLaps)
        .addEffect(new Wiggle(new PVector(12, 12, 12), 2, 7));

    // Impacts: a marker on each scene pin, and onLoop for the first one.
    for (int i = 1; i < 4; i++) {
        int k = i;
        tm.addMarker(names[k], scene[k], m -> impact(k));
    }
    tm.onLoop(t -> impact(0));

    // Dust around the stage, for parallax once the camera moves.
    randomSeed(3);
    dust = new PVector[260];
    for (int i = 0; i < dust.length; i++) {
        dust[i] = PVector.random3D(this).mult(random(240, 520));
    }
}

void impact(int i) {
    waves.add(new Shockwave(this, palette[i]));
}

void draw() {
    if (scrubbing) {
        float t = map(constrain(mouseX, barX0, barX1), barX0, barX1, 0, D);
        // true fires the markers on the way, so scrubbing still triggers impacts.
        tm.to(min(t, D - 0.001f), true);
    }

    int ac = accent.value();
    int back = bg;
    for (Shockwave w : waves) {
        back = lerpColor(back, w.col, w.flash.value() * 0.14f);
    }
    background(back);

    hint(DISABLE_DEPTH_TEST);
    blendMode(ADD);

    pushMatrix();
    translate(width / 2, height / 2 - 30);
    scale(zoom);
    cam.value().apply(this);
    rotateZ(spin.value());

    drawDust();
    drawFloor();
    drawCore(ac);
    drawMoons(ac);
    drawComet(ac);
    drawWaves();

    popMatrix();

    blendMode(BLEND);
    drawHud(ac);
}

void drawDust() {
    stroke(255, 70);
    strokeWeight(2);
    for (PVector p : dust) {
        point(p.x, p.y, p.z);
    }
}

// A polar grid below the shape. Seen flat it's a radar; tilted, a floor.
void drawFloor() {
    pushMatrix();
    translate(0, 0, -190);
    noFill();
    stroke(255, 22);
    strokeWeight(1);
    for (int r = 80; r <= 400; r += 80) {
        circle(0, 0, r * 2);
    }
    for (int i = 0; i < 16; i++) {
        float a = i * TWO_PI / 16;
        line(cos(a) * 80, sin(a) * 80, cos(a) * 400, sin(a) * 400);
    }
    popMatrix();
}

void drawCore(int ac) {
    // Echoes of the shape a moment ago: ghosting while it wobbles.
    List<ShapeMorph> ghosts = core.echo(6, 0.045f);
    noFill();
    strokeWeight(1.5f);
    for (int i = ghosts.size() - 1; i > 0; i--) {
        stroke(ac, 70 - i * 10);
        outline(ghosts.get(i));
    }

    ShapeMorph s = ghosts.get(0);
    stroke(255, 210);
    strokeWeight(2.5f);
    outline(s);

    // Stems down to the floor show the depth.
    stroke(ac, 40);
    strokeWeight(1);
    for (int i = 0; i < N; i += 6) {
        PVector p = s.points.get(i);
        line(p.x, p.y, p.z, p.x, p.y, -190);
    }

    stroke(ac, 230);
    strokeWeight(5);
    for (int i = 0; i < N; i += 6) {
        PVector p = s.points.get(i);
        point(p.x, p.y, p.z);
    }

    // Two sparks racing around the outline.
    float head = tm.t() / D * N * 3;
    for (int k = 0; k < 2; k++) {
        for (int j = 0; j < 14; j++) {
            int idx = ((int) head - j + k * N / 2 + N * 4) % N;
            PVector p = s.points.get(idx);
            stroke(255, 255 - j * 18);
            strokeWeight(9 - j * 0.5f);
            point(p.x, p.y, p.z);
        }
    }
}

void drawMoons(int ac) {
    for (Keyed<PVector> m : moons) {
        List<PVector> trail = m.echo(30, 0.022f);
        for (int i = trail.size() - 1; i > 0; i--) {
            PVector a = trail.get(i), b = trail.get(i - 1);
            stroke(ac, map(i, 0, trail.size(), 200, 0));
            strokeWeight(map(i, 0, trail.size(), 6, 1));
            line(a.x, a.y, a.z, b.x, b.y, b.z);
        }
        PVector p = trail.get(0);
        stroke(ac, 60);
        strokeWeight(26);
        point(p.x, p.y, p.z);
        stroke(255);
        strokeWeight(11);
        point(p.x, p.y, p.z);
    }
}

void drawComet(int ac) {
    List<PVector> tail = comet.echo(40, 0.018f);
    for (int i = tail.size() - 1; i > 0; i--) {
        PVector a = tail.get(i), b = tail.get(i - 1);
        stroke(255, map(i, 0, tail.size(), 220, 0));
        strokeWeight(map(i, 0, tail.size(), 8, 0.5f));
        line(a.x, a.y, a.z, b.x, b.y, b.z);
    }
    PVector p = tail.get(0);
    stroke(ac, 90);
    strokeWeight(30);
    point(p.x, p.y, p.z);
}

void drawWaves() {
    for (int i = waves.size() - 1; i >= 0; i--) {
        Shockwave w = waves.get(i);
        w.draw();
        if (w.isFinished()) {
            w.dispose();
            waves.remove(i);
        }
    }
}

void drawHud(int ac) {
    float t = tm.t();

    fill(255);
    textAlign(LEFT, TOP);
    textSize(18);
    text("KEYED", 40, 32);
    fill(255, 120);
    textSize(11);
    text("keyframes for Processing", 40, 56);

    textAlign(RIGHT, TOP);
    textSize(14);
    fill(255);
    text(nf(t, 2, 2) + " s", barX1, 32);
    fill(ac);
    textSize(11);
    text("zoom " + nf(zoom, 1, 2), barX1, 56);

    // The word, morphed by StringLerp under a Lag.
    fill(255);
    textAlign(LEFT, BOTTOM);
    textSize(40);
    text(word.value(), 40, 538);

    // value(t) reads any time, so the whole spring can be graphed.
    noFill();
    stroke(ac, 160);
    strokeWeight(1.5f);
    beginShape();
    for (float x = barX0; x <= barX1; x += 2) {
        float z = zoomKeyed.value(map(x, barX0, barX1, 0, D));
        vertex(x, map(z, 0.85f, 1.25f, 586, 552));
    }
    endShape();

    stroke(255, 50);
    strokeWeight(1);
    line(barX0, barY, barX1, barY);

    // A diamond per key of the hero shape, on the pins.
    noStroke();
    for (Key k : core.keys()) {
        float x = map(k.t(), 0, D, barX0, barX1);
        fill(255, 160);
        quad(x, barY - 5, x + 5, barY, x, barY + 5, x - 5, barY);
    }

    textAlign(CENTER, TOP);
    textSize(10);
    for (int i = 0; i < 4; i++) {
        float x = map(scene[i].t(), 0, D, barX0, barX1);
        fill(palette[i]);
        text(names[i], x, barY + 10);
    }

    float x = map(t, 0, D, barX0, barX1);
    stroke(ac);
    strokeWeight(2);
    line(x, barY - 14, x, barY + 6);
}

void outline(ShapeMorph s) {
    beginShape();
    s.vertices(this);
    endShape(CLOSE);
}

ShapeMorph rippled(ShapeMorph s, float t) {
    float amp = rippleAmp.value(t);
    int n = s.points.size();
    List<PVector> pts = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
        PVector p = s.points.get(i).copy();
        // A period of 2 seconds fits the 12 second loop 6 times.
        p.z += amp * sin(TWO_PI * (4f * i / n - t / 2));
        pts.add(p);
    }
    return new ShapeMorph(pts);
}

// All shapes have N points and start at the top, so they morph without twisting.

ShapeMorph circle(float r) {
    List<PVector> pts = new ArrayList<>();
    for (int i = 0; i < N; i++) {
        float a = -HALF_PI + i * TWO_PI / N;
        pts.add(new PVector(cos(a) * r, sin(a) * r, 0));
    }
    return new ShapeMorph(pts);
}

ShapeMorph knot(float s) {
    List<PVector> pts = new ArrayList<>();
    for (int i = 0; i < N; i++) {
        float t = i * TWO_PI / N;
        pts.add(new PVector(
            (sin(t) + 2 * sin(2 * t)) * s,
            (cos(t) - 2 * cos(2 * t)) * s,
            -sin(3 * t) * s * 1.3f));
    }
    return new ShapeMorph(pts);
}

ShapeMorph star(int spikes, float outer, float inner) {
    List<PVector> corners = new ArrayList<>();
    for (int i = 0; i < spikes * 2; i++) {
        float a = -HALF_PI + i * PI / spikes;
        float r = i % 2 == 0 ? outer : inner;
        corners.add(new PVector(cos(a) * r, sin(a) * r, 0));
    }
    // Evenly filled edges, so it has N points like the others.
    List<PVector> pts = new ArrayList<>();
    int perEdge = N / corners.size();
    for (int i = 0; i < corners.size(); i++) {
        PVector a = corners.get(i), b = corners.get((i + 1) % corners.size());
        for (int j = 0; j < perEdge; j++) {
            pts.add(PVector.lerp(a, b, j / (float) perEdge));
        }
    }
    return new ShapeMorph(pts);
}

ShapeMorph flower() {
    List<PVector> pts = new ArrayList<>();
    for (int i = 0; i < N; i++) {
        float a = -HALF_PI + i * TWO_PI / N;
        float r = 150 + 35 * cos(6 * (a + HALF_PI));
        pts.add(new PVector(cos(a) * r, sin(a) * r, 60 * cos(3 * (a + HALF_PI))));
    }
    return new ShapeMorph(pts);
}

// Same rotation as rotateX(x), rotateY(y), rotateZ(z).
Quaternion euler(float x, float y, float z) {
    return Quaternion.fromAxisAngle(new PVector(1, 0, 0), x)
        .mult(Quaternion.fromAxisAngle(new PVector(0, 1, 0), y))
        .mult(Quaternion.fromAxisAngle(new PVector(0, 0, 1), z));
}

void mousePressed() {
    if (mouseY > barY - 40) {
        scrubbing = true;
        tm.play(false);
    }
}

void mouseReleased() {
    if (scrubbing) {
        scrubbing = false;
        tm.play(true);
    }
}

void keyPressed() {
    if (key == ' ') {
        tm.play(!tm.isPlaying());
    }
}
