# 1. Getting Started

In this chapter you animate your first value, move things around with vectors, meet the other value types, and learn that an animation can be read at any point in time.

## Your first key

![FirstKey](../assets/gifs/FirstKey.gif){ .sketch }

Every Keyed sketch starts by connecting the library to the sketch in `setup()`:

```java
Keyed.init(this).setDuration(2);
```

`init()` hooks Keyed into Processing, so animations follow real time without you having to advance them. It returns the **default timeline**, which we tell to loop every 2 seconds.

Next, create an animated value. `Keyed.ofFloat()` makes an animated `float`. Then add **keys**: a time in seconds and the value at that time.

```java
Keyed<Float> x;

x = Keyed.ofFloat()
    .key(0, 50f)
    .key(1, 350f)
    .key(2, 50f);
```

At 0 seconds `x` is 50, at 1 second it's 350, and at 2 seconds it's back at 50. In between, Keyed blends the values. In `draw()`, `value()` gives you the value at the current time:

```java
circle(x.value(), height / 2, 40);
```

That's all there is to it. The rest of this tutorial adds to this pattern.

??? example "Full sketch: FirstKey"
    ```java
    --8<-- "01_Basics/FirstKey/FirstKey.pde"
    ```

## Vectors

![Vectors](../assets/gifs/Vectors.gif){ .sketch }

`Keyed.ofPVector()` animates a `PVector`, blending x, y and z together. Here the ball visits one corner per second. The last key returns to the first corner, so the loop closes without a jump:

```java
pos = Keyed.ofPVector();
for (int i = 0; i <= corners.length; i++) {
    pos.key(i, corners[i % corners.length]);
}
```

Keys store a **copy** of the vector you pass, so changing `corners` later won't move them.

??? example "Full sketch: Vectors"
    ```java
    --8<-- "01_Basics/Vectors/Vectors.pde"
    ```

## Value types

![ValueTypes](../assets/gifs/ValueTypes.gif){ .sketch }

Keyed knows how to blend more than numbers:

| Factory | Type | How it blends |
|---|---|---|
| `Keyed.ofFloat()` | `Float` | linearly |
| `Keyed.ofInt()` | `Integer` | linearly, rounded to the nearest int |
| `Keyed.ofColor()` | `Integer` | red, green, blue and alpha |
| `Keyed.ofPVector()` | `PVector` | x, y and z |
| `Keyed.ofString()` | `String` | morphs one character edit at a time |
| `Keyed.ofBoolean()` | `Boolean` | switches when the next key is reached |

Each factory also takes a default value, e.g. `Keyed.ofFloat(50)`, used while there are no keys. Without one it's 0, opaque black, `false`, an empty `String` or `(0, 0, 0)`. `Keyed.of(value)` is a shorthand that picks the type from the value, e.g. `Keyed.of(0f)` is the same as `Keyed.ofFloat(0)`; ints and colors always need `ofInt()` and `ofColor()`.

Colors are `int`s in Processing, so they need their own factory, `ofColor()`; otherwise they'd be blended as plain numbers.

```java
col = Keyed.ofColor()
    .key(0, color(230, 60, 60))
    .key(2, color(60, 120, 230))
    .key(4, color(230, 60, 60));

word = Keyed.ofString()
    .key(0, "keyed")
    .key(2, "animation")
    .key(4, "keyed");
```

Want to blend something else? See [Types](types.md).

??? example "Full sketch: ValueTypes"
    ```java
    --8<-- "01_Basics/ValueTypes/ValueTypes.pde"
    ```

## Any time

![AnyTime](../assets/gifs/AnyTime.gif){ .sketch }

`value()` is the value **now**. `value(t)` is the value at **any** time `t`, past or future. Here it's used to draw the whole animation as a graph, one point per pixel:

```java
beginShape();
for (int x = (int) gx0; x <= gx1; x++) {
    float t = map(x, gx0, gx1, 0, duration);
    vertex(x, ballY.value(t));
}
endShape();
```

`keys()` lists the keys, and each key's `t()` is its time, which is handy for drawing them:

```java
for (Key k : ballY.keys()) {
    circle(tx(k.t()), ballY.value(k.t()), 8);
}
```

The current time of the default timeline is `Keyed.defaultTimeline().t()`. Hover over the graph in the sketch to read the value at any other time.

??? example "Full sketch: AnyTime"
    ```java
    --8<-- "01_Basics/AnyTime/AnyTime.pde"
    ```

Next: [Easing](easing.md).
