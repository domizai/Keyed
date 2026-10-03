// A reusable burst: each instance has its own timeline, starting at 0 when created.
class MyComposition extends Composition {
  static final float DURATION = 1;

  List<Component> used = new ArrayList<>();
  List<Keyed<PVector>> pos = new ArrayList<>();
  PVector origin;

  MyComposition(List<Component> components, int usedSize, PVector origin) {
    super(DURATION);
    this.origin = origin;
    for (int i = 0; i < usedSize; i++) {
      used.add(components.get(i % components.size()));
      pos.add(keyed(new PVectorLerp(), new PVector())
        .key(0, new PVector())
        .key(Key.at(DURATION).setEasing(5/6f), new PVector(random(-50, 50), random(-50, 50))));
    }
  }

  void draw() {
    float t = 1 - timeline().t() / DURATION;
    for (int i = 0; i < pos.size(); i++) {
      Component c = used.get(i);
      c.setColor(color(RED, 1, 1, Easing.quadOut(t)));
      c.draw(pos.get(i).value().add(origin));
    }
  }
}

abstract class Component {
  int c = color(0);

  abstract void draw(PVector position);

  void setColor(int color) {
    c = color;
  }
}

class CircleComponent extends Component {
  float radius;

  CircleComponent(float radius) {
    this.radius = radius;
  }

  void draw(PVector position) {
    pushStyle();
    fill(c);
    ellipse(position.x, position.y, radius, radius);
    popStyle();
  }
}

class SquareComponent extends Component {
  float size;

  SquareComponent(float size) {
    this.size = size;
  }

  void draw(PVector position) {
    pushStyle();
    fill(c);
    rect(position.x, position.y, size, size);
    popStyle();
  }
}
