// A custom type: implement Lerpable and Keyed.of() can animate it.
// Immutable, so lerp() can safely share instances.
class Transform implements Lerpable<Transform> {
  final float x, y, rotation, scale;

  Transform(float x, float y, float rotation, float scale) {
    this.x = x;
    this.y = y;
    this.rotation = rotation;
    this.scale = scale;
  }

  Transform(float x, float y) {
    this(x, y, 0, 1);
  }

  // Rotation takes the shorter way round, e.g. 350° to 10° turns 20°, not 340°.
  public Transform lerp(Transform b, float d) {
    float turn = ((b.rotation - rotation) % TWO_PI + TWO_PI + PI) % TWO_PI - PI;
    // PApplet. is needed: this class's own lerp() hides the sketch's.
    return new Transform(
      PApplet.lerp(x, b.x, d),
      PApplet.lerp(y, b.y, d),
      rotation + turn * d,
      PApplet.lerp(scale, b.scale, d));
  }

  void apply(PApplet g) {
    g.translate(x, y);
    g.rotate(rotation);
    g.scale(scale);
  }
}
