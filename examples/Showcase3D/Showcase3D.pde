import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.tween.*;
import ch.domizai.keyed.types.*;

import java.util.List;

Timeline tm;
float fps = 30f;
ArrayList<Pin> pins = new ArrayList<>();
CubicBezier path;
Keyed<PVector> pos;
Keyed<ShapeMorph> shape;
Keyed<Quaternion> orientation;

void settings() {
  size(400, 400, P3D);
  pixelDensity(displayDensity());
}

void setup() {
  colorMode(HSB, 1, 1, 1, 1); // hue, saturation, brightness, alpha
  noStroke();
  frameRate(fps);

  // Order matters: configure Keyed before creating timelines or keyed values.
  Keyed.init(this);
  Keyed.setFrameRate(fps);
  Keyed.sync(false);
  Keyed.setUnit(Keyed.SECOND);
  Keyed.autoplay(true);

  // Timeline (seconds)
  tm = new Timeline();
  int duration = 10;
  tm.setDuration(duration);
  for (int f = 0; f < duration; f++) pins.add(Pin.at(f));

  // Follows a 3D curve while orbiting around a tilted axis.
  path = new CubicBezier(
    new PVector(-120, 100, -100),
    new PVector(120, 100, 150),
    new PVector(-120, -100, 150),
    new PVector(120, -100, -100));

  pos = Keyed.of(new PVector())
    .setTimeline(tm)
    .key(Key.at(pins.get(0)).setEasing(1/3f), path.at(0f))
    .key(Key.at(pins.get(9)).setEasing(1/3f), path.at(1f))
    .addEffect(Effect.orbit(20, 1, new PVector(1, 1, 0)));

  // A flat ring morphing into a crown; z is kept.
  ShapeMorph ring = new ShapeMorph(crown(40, 60, 0));
  ShapeMorph crown = new ShapeMorph(crown(10, 60, 40));
  shape = Keyed.of(ring)
    .setTimeline(tm)
    .key(Key.at(pins.get(1)).setEasing(1/3f), ring)
    .key(Key.at(pins.get(5)).setEasing(1/3f), crown)
    .key(Key.at(pins.get(9)).setEasing(1/3f), ring);

  // Rotations around different axes; slerp blends them along the shortest arc.
  Quaternion flat = Quaternion.identity();
  Quaternion tipped = Quaternion.fromAxisAngle(new PVector(1, 0, 0), HALF_PI);
  Quaternion flipped = Quaternion.fromAxisAngle(new PVector(0, 1, 1), PI);
  Quaternion twisted = Quaternion.fromAxisAngle(new PVector(1, 1, 1), TWO_PI / 3);
  orientation = Keyed.of(flat)
    .setTimeline(tm)
    .key(Key.at(pins.get(0)).setEasing(1/3f), flat)
    .key(Key.at(pins.get(3)).setEasing(1/3f), tipped)
    .key(Key.at(pins.get(5)).setEasing(1/3f), flipped)
    .key(Key.at(pins.get(7)).setEasing(1/3f), twisted)
    .key(Key.at(pins.get(9)).setEasing(1/3f), flat);
}

void draw() {
  background(1);
  lights();
  translate(width / 2, height / 2);
  rotateX(-0.4f);
  rotateY(frameCount * 0.01f);

  pushStyle();
  noFill();
  stroke(0, 0.3f);
  beginShape();
  for (int i = 0; i <= 100; i++) {
    PVector q = path.value(i / 100f);
    vertex(q.x, q.y, q.z);
  }
  endShape();
  popStyle();

  PVector p = pos.value();
  Quaternion q = orientation.value();
  PVector axis = q.axis();
  pushMatrix();
  translate(p.x, p.y, p.z);
  rotate(q.angle(), axis.x, axis.y, axis.z);
  fill(2/3f, 1, 1);
  box(40, 20, 8);
  popMatrix();

  pushStyle();
  pushMatrix();
  rotateX(HALF_PI);
  noFill();
  stroke(0, 1, 1);
  strokeWeight(2);
  beginShape();
  shape.value().vertices(this);
  endShape(CLOSE);
  popMatrix();
  popStyle();
}

// Points on a circle whose z alternates between 0 and spike, like the spikes of a crown.
List<PVector> crown(int count, float radius, float spike) {
  List<PVector> pts = new ArrayList<>();
  for (int i = 0; i < count; i++) {
    float a = TWO_PI * i / count;
    pts.add(new PVector(cos(a) * radius, sin(a) * radius, i % 2 == 0 ? spike : 0));
  }
  return pts;
}
