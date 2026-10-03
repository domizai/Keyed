import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.lerps.*;
import ch.domizai.keyed.tween.*;
import ch.domizai.keyed.types.*;

import java.util.List;

Timeline tm;
Keyed<PVector> pos;
Keyed<Float> rot;
PVector vec = new PVector(10, 20);
Keyed<Float> vecK;
Keyed<String> text;
Keyed<ShapeMorph> shape;
Keyed<Transform> transform;
Keyed<Integer> squareColor;
List<PVector> obstacles = new ArrayList<>();
float obstaclesRadius = 80;
int obstaclesCount = 4;

float fps = 30f;
ArrayList<Pin> pins = new ArrayList<>();

float RED = 0;
float GREEN = 1f/3f;
float BLUE = 2f/3f;

List<PVector> points = new ArrayList<>();
Spline spline;

List<Component> components = new ArrayList<>();
List<MyComposition> compositions = new ArrayList<>();

float off = 30f;
float flash = 0;

void settings() {
  size(400, 400);
  pixelDensity(displayDensity());
}

void setup() {
  colorMode(HSB, 1, 1, 1, 1); // hue, saturation, brightness, alpha
  rectMode(CENTER);
  ellipseMode(CENTER);
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

  // Markers
  tm.addMarker("start", 0, this::onStart);
  tm.addMarker("star", pins.get(4), m -> flash = 1);
  tm.addMarker("triangle", pins.get(7), m -> flash = 1);
  tm.onLoop(t -> println("loop"));

  // Keyed values
  rot = Keyed.of(0f)
    .setTimeline(tm)
    .key(Key.at(pins.get(0)).setEasing(Easing.CUBIC_IN_OUT), 0f)
    .key(pins.get(duration-1), TWO_PI);

  shufflePoints();
  spline = new Spline(points).setTightness(0);
  pos = Keyed.of(new PVector(width/2, height/2))
    .setTimeline(tm)
    .key(Key.at(pins.get(1)).setEasing(1/3f), spline.at(0.0f))
    .key(Key.at(pins.get(duration-1)).setEasing(1/3f), spline.at(1.0f));

  // Animates a field of an existing object.
  Tween<Float> splineX = spline.x();
  vecK = Keyed.bind(vec, "x")
    .setTimeline(tm)
    .key(Key.at(pins.get(1)).setEasing(1/3f), splineX.at(0.0f))
    .key(Key.at(pins.get(3)).setEasing(1/3f), splineX.at(1.0f));

  text = Keyed.of("")
    .setTimeline(tm)
    .key(pins.get(0), "")
    .key(pins.get(2), "Hello Keyed")
    .key(pins.get(4), "Hello Keyed")
    .key(pins.get(6), "Hello World")
    .key(pins.get(8), "Hello World")
    .key(pins.get(9), "");

  ShapeMorph circle = new ShapeMorph(polygon(40, 30, 30));
  ShapeMorph star = new ShapeMorph(polygon(10, 35, 15));
  ShapeMorph triangle = new ShapeMorph(polygon(3, 35, 35));
  shape = Keyed.of(circle)
    .setTimeline(tm)
    .key(Key.at(pins.get(1)).setEasing(1/3f), circle)
    .key(Key.at(pins.get(4)).setEasing(1/3f), star)
    .key(Key.at(pins.get(7)).setEasing(1/3f), triangle)
    .key(Key.at(pins.get(9)).setEasing(1/3f), circle);

  float cx = width - 60, cy = height - 60;
  transform = Keyed.of(new Transform(cx, cy))
    .setTimeline(tm)
    .key(Key.at(pins.get(1)).setEasing(1/3f), new Transform(cx, cy, 0, 1))
    .key(Key.at(pins.get(4)).setEasing(1/3f), new Transform(cx - 40, cy, radians(170), 1.5f))
    // hold() keeps the value until the next key, ignoring easing out.
    .key(Key.at(pins.get(6)).setEasingIn(1/3f).hold(), new Transform(cx - 40, cy - 40, radians(-170), 0.5f))
    .key(Key.at(pins.get(9)).setEasing(1/3f), new Transform(cx, cy, 0, 1));

  // Jumps between colors at each key instead of blending.
  squareColor = new Keyed<>(new StepLerp<Integer>(), color(0))
    .setTimeline(tm)
    .key(pins.get(0), color(0))
    .key(pins.get(3), color(RED, 1, 1))
    .key(pins.get(5), color(GREEN, 1, 0.8f))
    .key(pins.get(7), color(0));

  // Built-in effects; try one at a time.
  // pos.addEffect(Effect.wiggle(30f, 3f));
  // pos.addEffect(Effect.orbit(20f, 0.3f));
  // pos.addEffect(Effect.gridSnap(20));
  // pos.addEffect(new StopMotion<>(4f / fps));
  // pos.addEffect(Effect.spring(1.5f, 0.05f));
  // pos.addEffect(Effect.lag(0.5f, 10));

  // Custom effect: pushes the position out of the obstacles.
  initObstacles();
  pos.addEffect((PVector v, float t) -> {
    PVector p = v.copy();
    // Repeated so pushing out of one obstacle into an overlapping one still ends outside both.
    for (int iter = 0; iter < obstacles.size(); iter++) {
      boolean pushed = false;
      for (PVector obstacle : obstacles) {
        PVector away = PVector.sub(p, obstacle);
        float d = away.mag();
        if (d < obstaclesRadius - 0.001f) {
          if (d == 0) away.set(1, 0);
          p = PVector.add(obstacle, away.setMag(obstaclesRadius));
          pushed = true;
        }
      }
      if (!pushed) break;
    }
    return p;
  });

  components.add(new CircleComponent(5));
  components.add(new SquareComponent(7));
}

PVector posPrev;

void draw() {
  background(BLUE, 0.3f * flash, 1);
  flash *= 0.85f;

  pushStyle();
  int steps = 200;
  fill(0, 0, 0, 0.3f);
  for (int i = 0; i < points.size() * steps; i++) {
    PVector pt = spline.value(i / (float) steps);
    ellipse(pt.x, pt.y, 3, 3);
  }
  fill(0, 0, 0, 0.9f);
  for (int i = 1; i < points.size() - 1; i++) {
    PVector pt = points.get(i);
    ellipse(pt.x, pt.y, 7, 7);
  }
  popStyle();

  // Trail of past positions.
  pushStyle();
  List<PVector> trail = pos.echo(100, 0.3f / fps);
  for (int i = 0; i < trail.size(); i++) {
    PVector q = trail.get(i);
    float d = map(i, 0, trail.size(), 1f, 0.1f);
    pushMatrix();
    translate(q.x, q.y);
    fill(d, 1, 1, 0.6f);
    scale(d);
    rotate(rot.value());
    rect(0, 0, 23, 23);
    popMatrix();
  }
  popStyle();

  // Shrinks with speed.
  PVector p = pos.value();
  if (posPrev == null) posPrev = p.copy();
  float size = map(constrain(posPrev.dist(p), 0, 30), 0, 30, 31, 11);
  posPrev = p.copy();
  pushMatrix();
  translate(p.x, p.y);
  rotate(rot.value());
  pushStyle();
  fill(squareColor.value());
  rect(0, 0, size, size);
  popStyle();
  popMatrix();

  fill(0);
  ellipse(vec.x, vec.y, 20, 20);

  pushStyle();
  fill(0);
  textSize(16);
  boolean cursorOn = frameCount / (int) (fps / 2) % 2 == 0;
  text(text.value() + (cursorOn ? "_" : ""), 20, height - 20);
  popStyle();

  pushStyle();
  fill(BLUE, 1, 1, 0.8f);
  pushMatrix();
  translate(width - 60, 60);
  beginShape();
  shape.value().vertices(this);
  endShape(CLOSE);
  popMatrix();
  popStyle();

  pushMatrix();
  pushStyle();
  transform.value().apply(this);
  fill(GREEN, 1, 0.8f, 0.8f);
  rect(0, 0, 30, 30);
  // Marks the top edge so the rotation direction is visible.
  fill(0);
  rect(0, -12, 30, 6);
  popStyle();
  popMatrix();

  for (int i = compositions.size() - 1; i >= 0; i--) {
    MyComposition c = compositions.get(i);
    c.draw();
    if (c.isFinished()) {
      c.dispose();
      compositions.remove(i);
    }
  }

  pushStyle();
  noFill();
  stroke(0, 1, 1, 0.5f);
  for (PVector obstacle : obstacles)
    ellipse(obstacle.x, obstacle.y, obstaclesRadius * 2, obstaclesRadius * 2);
  popStyle();
}

void onStart(Marker m) {
  println(m.name() + " at " + m.t());
}

// Scrubs the timeline with the mouse when autoplay is off.
void mouseMoved() {
  if (!tm.isAutoplay())
    tm.to(map(mouseX, 0, width, 0, tm.duration()), true);
}

// Click to shuffle the path and spawn a burst.
void mouseClicked() {
  shufflePoints();
  initObstacles();
  spline.setPoints(points);
  MyComposition comp = new MyComposition(components, 20, new PVector(mouseX, mouseY));
  comp.timeline().autoplay(true);
  compositions.add(comp);
}

// Alternates between the outer and inner radius; starts at the top so shapes line up when morphing.
List<PVector> polygon(int count, float outer, float inner) {
  List<PVector> pts = new ArrayList<>();
  for (int i = 0; i < count; i++) {
    float a = -HALF_PI + TWO_PI * i / count;
    float r = i % 2 == 0 ? outer : inner;
    pts.add(new PVector(cos(a) * r, sin(a) * r));
  }
  return pts;
}

void shufflePoints() {
  points.clear();
  for (int i = 0; i < 10; i++)
    points.add(new PVector(random(off, width-off), random(off, height-off)));
}

void initObstacles() {
  obstacles.clear();
  for (int i = 0; i < obstaclesCount; i++)
    obstacles.add(new PVector(random(0, width), random(0, height)));
}
