
The following dummy code is a sketch for the api.

# Splines and Curves

```java
Path path0 = new CubicBezier(a, b, c, d);
Path path1 = new QuadraticBezier(d, e, f);
Path path2 = new CubicBezier(f, g, h, i);
Curve curve0 = path0.add(path1).add(path2).slice(0.25f, 1);
Curve curve1 = new Curve(path1, path2).slice(0, 0.75f);
pos.key(frames.get(1), curve.at(0f));

Spline s = new Spline(ctrlStart, a, b, c, d, ctrlEnd).setTightness(0);
pos.key(frames.get(1), s.at(0f));
```
