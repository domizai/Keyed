import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;

int grid = 20;
Keyed<PVector> smooth, snapped, snappedX;

void settings() {
    size(400, 400);
}

void setup() {
    Keyed.init(this).setDuration(3);
    textFont(createFont("Courier", 14));
    rectMode(CENTER);

    smooth = motion();

    // GridSnap(size) rounds x and y to multiples of size,
    // so the motion jumps from cell to cell.
    snapped = motion().addEffect(new GridSnap(grid));

    // GridSnap(sizeX, sizeY) uses a size per axis; 0 leaves that axis alone.
    // This one steps sideways but moves smoothly up and down.
    snappedX = motion().addEffect(new GridSnap(grid, 0));
}

Keyed<PVector> motion() {
    return Keyed.ofPVector()
        .key(Key.at(0).setEasing(1 / 3f), new PVector(80, 300))
        .key(Key.at(1).setEasing(1 / 3f), new PVector(200, 80))
        .key(Key.at(2).setEasing(1 / 3f), new PVector(320, 300))
        .key(Key.at(3).setEasing(1 / 3f), new PVector(80, 300));
}

void draw() {
    background(255);

    // Grid lines between the snapped positions, so each one is a cell.
    stroke(240);
    for (int i = grid / 2; i < width; i += grid) {
        line(i, 0, i, height);
        line(0, i, width, i);
    }

    noStroke();
    PVector s = snapped.value();
    fill(0);
    rect(s.x, s.y, grid, grid);

    PVector x = snappedX.value();
    fill(230, 60, 60);
    circle(x.x, x.y, grid * 0.8f);

    PVector p = smooth.value();
    noFill();
    stroke(150);
    circle(p.x, p.y, grid);

    noStroke();
    fill(150);
    text("no effect", 20, 345);
    fill(0);
    text("new GridSnap(grid)", 20, 365);
    fill(230, 60, 60);
    text("new GridSnap(grid, 0)", 20, 385);
}
