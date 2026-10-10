# Keyed

Keyframe animation for Processing. Set a value at a few points in time, and Keyed blends everything in between, with easing, paths, effects and events.

👉 [Documentation and Tutorial](https://domizai.github.io/Keyed/)

Using p5.js? There is also [p5.keyed](https://github.com/domizai/p5.keyed), the same library for p5.js.

![Showcase](mkdocs/assets/gifs/Showcase.gif)

```java
import ch.domizai.keyed.*;

Keyed<Float> x;

void setup() {
    size(400, 400);
    Keyed.init(this).setDuration(2);

    x = Keyed.ofFloat()
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

The library comes with many examples, grouped like the chapters of the [tutorial](https://domizai.github.io/Keyed/). Find them in Processing under *File > Examples > Contributed Libraries > Keyed*.

## Development

Needs Java 17, which Gradle downloads if it's missing, and Processing 4. The library is in `src/main/java`, the examples in `examples/`. On Windows, use `gradlew.bat` instead of `./gradlew`.

```bash
./gradlew stageRelease           # build/release/Keyed: the library folder as Processing expects it
./gradlew buildReleaseArtifacts  # release/Keyed.zip, Keyed.pdex and Keyed.txt
```

To try the current code in Processing, link `build/release/Keyed` into the `libraries` folder of your sketchbook once (its location is shown in Processing's Preferences). Then rerun `./gradlew stageRelease` after each change and restart Processing.

```bash
# macOS and Linux
ln -s "$PWD/build/release/Keyed" "<sketchbook>/libraries/Keyed"
# Windows (cmd)
mklink /J "<sketchbook>\libraries\Keyed" "%CD%\build\release\Keyed"
```

Examples can also be run from the terminal with Processing's command-line mode, where `<processing>` is the Processing app's executable (on macOS `/Applications/Processing.app/Contents/MacOS/Processing`). The sketch path must be absolute:

```bash
<processing> cli --sketch="<path to Keyed>/examples/01_Basics/FirstKey" --run
```

The documentation is built with MkDocs Material (`mkdocs.yml`, sources in `mkdocs/`). `mkdocs build` writes the site to `docs/`, which GitHub Pages serves.

```bash
uv sync
uv run mkdocs serve  # or: uv run mkdocs build
```

* * *

Tested with Processing 4.5.7 on macOS 26.4.

AI coding assistants were used for parts of the code, debugging, documentation, code examples, and code reviews. All changes were reviewed and tested before being released.
