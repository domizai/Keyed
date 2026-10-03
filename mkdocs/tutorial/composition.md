# 9. Composition

So far all animations were set up once in `setup()`. But what if you need the same animation many times, starting at different moments, like a burst on every click? A **`Composition`** is a reusable, self-contained animation on its own timeline.

## Click burst

![ClickBurst](../assets/gifs/ClickBurst.gif){ .sketch }

Extend `Composition`, pass its duration to `super()`, and create its values with `add()`. Key times are **local**: 0 is when the composition was created.

```java
class Burst extends Composition {
    PApplet g;
    float x, y;
    Keyed<Float> ring, fade;

    Burst(PApplet g, float x, float y) {
        // Lasts 0.8 seconds, on a timeline of its own that plays once.
        super(0.8f);
        this.g = g;
        this.x = x;
        this.y = y;

        ring = add(Keyed.of(0f)
            .key(Key.at(0).setEasing(Easing.CUBIC_OUT), 0f)
            .key(0.8f, 90f));

        fade = add(Keyed.of(0f)
            .key(0.3f, 255f)
            .key(0.8f, 0f));
    }

    void draw() {
        g.noFill();
        g.stroke(0, fade.value());
        g.circle(x, y, ring.value() * 2);
    }
}
```

Each new `Burst` starts playing right away. Once it `isFinished()`, `dispose()` stops its timeline so it can be garbage collected:

```java
void mousePressed() {
    bursts.add(new Burst(this, mouseX, mouseY));
}

void draw() {
    background(255);
    for (int i = bursts.size() - 1; i >= 0; i--) {
        Burst b = bursts.get(i);
        b.draw();
        if (b.isFinished()) {
            b.dispose();
            bursts.remove(i);
        }
    }
}
```

Call `play()` to restart a composition from the beginning, and `timeline()` to control it like any other [timeline](timeline.md).

The sketch also spawns a burst every 0.6 seconds with `onLoop()`, so there is something to see without clicking.

??? example "Full sketch: ClickBurst"
    === "ClickBurst.pde"
        ```java
        --8<-- "Composition/ClickBurst/ClickBurst.pde"
        ```
    === "Burst.pde"
        ```java
        --8<-- "Composition/ClickBurst/Burst.pde"
        ```

Next: [Export](export.md).
