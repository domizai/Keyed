import ch.domizai.keyed.*;
import ch.domizai.keyed.types.*;

Keyed<PVector> euler;
Keyed<Quaternion> quat;

void settings() {
    size(400, 400, P3D);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));
    textAlign(CENTER, CENTER);

    // Both boxes turn between the same two orientations:
    // no rotation, and rotateX(PI) plus rotateZ(PI).

    // Blending the three angles one by one tumbles along the way.
    euler = Keyed.of(new PVector())
        .key(Key.at(0).setEasing(1 / 3f), new PVector(0, 0, 0))
        .key(Key.at(1.5f).setEasing(1 / 3f), new PVector(PI, 0, PI))
        .key(Key.at(3).setEasing(1 / 3f), new PVector(0, 0, 0));

    // A Quaternion is a 3D rotation that blends along the shortest arc.
    // Here it turns straight around the y axis, which is the same end result.
    quat = Keyed.of(Quaternion.identity())
        .key(Key.at(0).setEasing(1 / 3f), Quaternion.identity())
        .key(Key.at(1.5f).setEasing(1 / 3f), fromEuler(PI, 0, PI))
        .key(Key.at(3).setEasing(1 / 3f), Quaternion.identity());
}

// The same rotation as rotateX(x), rotateY(y), rotateZ(z).
Quaternion fromEuler(float x, float y, float z) {
    Quaternion qx = Quaternion.fromAxisAngle(new PVector(1, 0, 0), x);
    Quaternion qy = Quaternion.fromAxisAngle(new PVector(0, 1, 0), y);
    Quaternion qz = Quaternion.fromAxisAngle(new PVector(0, 0, 1), z);
    return qx.mult(qy).mult(qz);
}

void draw() {
    background(255);
    lights();

    PVector e = euler.value();
    pushMatrix();
    translate(110, 190);
    rotateX(e.x);
    rotateY(e.y);
    rotateZ(e.z);
    shape3D();
    popMatrix();

    pushMatrix();
    translate(290, 190);
    // Rotates the matrix, like rotate(angle, x, y, z).
    quat.value().apply(this);
    shape3D();
    popMatrix();

    fill(150);
    text("Euler angles", 110, 330);
    text("Quaternion", 290, 330);
}

// A box with a red nose, to see which way it faces.
void shape3D() {
    noStroke();
    fill(240);
    box(70, 40, 40);
    fill(230, 60, 60);
    translate(45, 0, 0);
    box(20, 20, 20);
}
