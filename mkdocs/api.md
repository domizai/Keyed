# API Overview

A compact summary of the library. Each section links to the tutorial chapter that explains it with examples.

## Setup and the clock

- **`Keyed.init(this)`**, called in `setup()`, hooks into Processing so animations follow real time. It returns the **default timeline**, used by every value that isn't given a timeline.
- **Global settings**, set before creating timelines:
    - `Keyed.setUnit(Keyed.SECOND | Keyed.FRAME)` measures times in seconds or frames.
    - `Keyed.setFrameRate(fps)` sets the frame rate used to convert between frames and seconds.
    - `Keyed.sync(bool)`: `true` follows real time; `false` advances exactly one frame per `draw()`, for repeatable exports.
    - `Keyed.autoplay(bool)` turns automatic advancing on or off.

See [Getting Started](tutorial/basics.md) and [Timelines](tutorial/timeline.md#frames-instead-of-seconds).

## Animated values: `Keyed<A>`

- **Built-in types:** `Keyed.of(float)`, `of(PVector)`, `of(String)`, `of(boolean)`, `ofInt(int)`, `ofColor(int)`.
- **Any type:** `new Keyed<>(lerp, default)`, or `Keyed.of(obj)` for `Lerpable` types.
- **Keys:** `key(t, value)`, `removeKey(...)`, `clearKeys()`, `keys()`.
- **Reading:** `value()` is the value now; `value(t)` the value at any time.
- **`echo(samples, delay)`** returns the value at several past (or future) times, e.g. for motion trails.
- **`setTimeline(tm)`** attaches the value to a timeline other than the default.

See [Getting Started](tutorial/basics.md).

## Keys, pins and easing

- **`Key.at(t)`** is a key with its own easing:
    - Influence, After Effects style: `setEasing(1 / 3f)`, `setEasingIn(...)`, `setEasingOut(...)`.
    - Curves: `setEasing(Easing.CUBIC_IN_OUT)` and other `QUAD`/`CUBIC`/`QUART`/`QUINT` presets, `SMOOTHSTEP`, `HOLD`.
    - `hold()` keeps the value until the next key.
- **Custom easing:** `Easing.cubicBezier(...)`, `quadraticBezier(...)`, `smoothstep(...)`, `steps(n)`, `powerIn`/`powerOut`/`powerInOut(p)`, or any lambda `d -> ...`.
- **`Pin.at(t)`** is a point in time shared by keys and markers. `pin.to(t)` moves all of them; `addListener()` reports moves.

See [Easing](tutorial/easing.md) and [Pins](tutorial/timeline.md#pins).

## Timeline

- **Creating:** `new Timeline()`, then `value.setTimeline(tm)`. Several timelines are independent clocks.
- **Duration:** `setDuration(d)` loops; `setDuration(d, false)` plays once. `loop(bool)`, `isLooping()`, `isFinished()`.
- **Playback:** `play(bool)`, `isPlaying()`, `setSpeed(s)` (negative plays backwards), `to(t)` to jump, `to(t, true)` to jump and fire markers on the way, `step()` to advance by hand.
- **Timing:** `t()` is the current time, `t(offset)` a time relative to it. `sync(bool)` and `setFixedStep(dt)` for exact stepping.
- **`dispose()`** stops a timeline you no longer need.

See [Timelines](tutorial/timeline.md).

## Events

- **Markers:** `addMarker([name,] t or pin, callback)`, `marker(name)`, `markers()`, `removeMarker(m)`.
- **Listeners:** `onLoop(callback)`, `onFinish(callback)`, `removeListener(callback)`.
- Markers fire on playback in either direction, also when a loop wraps around. A plain `to(t)` fires nothing.

See [Events](tutorial/events.md).

## Binding

- **`Keyed.bind(obj, "field")`** writes the animated value into a float field before every `draw()`.
- **`Keyed.bind(lerp, default, setter)`** or **`keyed.bind(setter)`** passes it to a setter.
- **`unbind()`** stops updating; **`apply()`** writes the value immediately.

See [Binding](tutorial/binding.md).

## Tweens and paths

- **`Tween<T>`** is a value as a function of `d` from 0 to 1. A key can follow a tween instead of a fixed value; `tween.at(d)` fixes a key to one position.
- `map(f)` converts a tween's values; `x()` and `y()` give one coordinate of a path.
- **Paths:** `QuadraticBezier`, `CubicBezier` (both at constant speed), `Spline` (like `curveVertex()`, with `setTightness()`), and `Curve` to chain them with `add()`.
- **`slice(t0, t1)`** is part of a path; swapping the ends reverses it.

See [Paths](tutorial/paths.md).

## Effects

Added with `addEffect(...)`, applied in the order added.

- **Value effects:** `Wiggle`, `Orbit`, `GridSnap`.
- **Time effects:** `Spring`, `Lag`, `StopMotion`.
- **Shortcuts for `PVector`s:** `Effect.wiggle(...)`, `orbit(...)`, `gridSnap(...)`, `spring(...)`, `lag(...)`.
- **Custom:** lambdas `(value, t) -> ...` as `Effect`, or `(source, t) -> ...` as `TimeEffect`.

See [Effects](tutorial/effects.md).

## Lerps and types

- **Lerps:** `FloatLerp`, `IntLerp`, `PVectorLerp`, `ColorLerp`, `AngleLerp`, `StringLerp`, `BooleanLerp`, `StepLerp`, or any lambda `(a, b, d) -> ...`.
- **`Lerpable<T>`:** a type that blends itself.
- **`Quaternion`:** 3D rotation without gimbal lock.
- **`ShapeMorph`:** morphs between polygons with different point counts, in 2D or 3D.

See [Types](tutorial/types.md).

## Composition

- Subclass `Composition` for a reusable animation on its own one-shot timeline. Key times are local.
- `add(keyed)`, `play()`, `isFinished()`, `dispose()`, `timeline()`.

See [Composition](tutorial/composition.md).
