# Keyed

Keyframe animation for Processing. Set a value at a few points in time, and Keyed blends everything in between, with easing, paths, effects and events.

👉 [Documentation](https://domizai.github.io/Keyed/)

![Showcase](mkdocs/assets/gifs/Showcase.gif)

```java
import ch.domizai.keyed.*;

Keyed<Float> x;

void setup() {
    size(400, 400);
    Keyed.init(this).setDuration(2);

    x = Keyed.of(0f)
        .key(0, 50f)
        .key(1, 350f)
        .key(2, 50f);
}

void draw() {
    background(255);
    circle(x.value(), height / 2, 40);
}
```

## Features

- **Any type:** floats, ints, colors, `PVector`, `String`, `boolean`, quaternions, shapes, or your own classes.
- **Easing:** After Effects style influence, the usual presets, CSS-like `cubicBezier()`, steps, or any function.
- **Timelines:** loop, play once, pause, scrub, change speed, play backwards, or run several clocks side by side.
- **Pins:** retime many keys at once by moving a single point in time.
- **Events:** markers and loop/finish callbacks.
- **Binding:** let Keyed write animated values straight into your fields.
- **Paths:** move along Béziers, splines and chained curves, in 2D or 3D.
- **Effects:** wiggle, orbit, spring, lag, stop motion, grid snap, motion trails, or your own.
- **Composition:** reusable, self-contained animations on their own timeline.
- **Export:** frame-exact timing for `saveFrame()`.

## Installation

Download [Keyed.pdex](https://github.com/domizai/Keyed/releases/latest/download/Keyed.pdex) and double-click it. Processing opens and offers to install the library.

Or download [Keyed.zip](https://github.com/domizai/Keyed/releases/latest/download/Keyed.zip), unzip it and put the `Keyed` folder into the `libraries` folder of your sketchbook (by default `Documents/Processing/libraries`). Restart Processing afterwards.

## Examples

The library comes with 40 examples, grouped like the chapters of the [tutorial](https://domizai.github.io/Keyed/). Find them in Processing under *File > Examples > Contributed Libraries > Keyed*.

* * *

Tested with Processing 4.5.7 on macOS 26.4.
