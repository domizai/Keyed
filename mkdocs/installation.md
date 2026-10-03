# Installation

The Keyed library cannot be installed through the Processing Library Manager yet and must be installed manually.

## One-click install

Download [Keyed.pdex](https://github.com/domizai/Keyed/releases/latest/download/Keyed.pdex) and double-click it. Processing opens and asks whether to install the library.

## Manual install

Download [Keyed.zip](https://github.com/domizai/Keyed/releases/latest/download/Keyed.zip). Unzip the file and put the extracted `Keyed` folder into the `libraries` folder of your sketchbook. By default this folder can be found in `Documents/Processing/libraries`.

Restart Processing afterwards.

## Usage

Import the library at the top of your sketch:

```java
import ch.domizai.keyed.*;
```

Some features live in their own packages:

```java
import ch.domizai.keyed.effect.*;  // Wiggle, Orbit, Spring, Lag, ...
import ch.domizai.keyed.lerps.*;   // ColorLerp, AngleLerp, StepLerp, ...
import ch.domizai.keyed.tween.*;   // CubicBezier, Spline, Curve, ...
import ch.domizai.keyed.types.*;   // Quaternion, ShapeMorph, Lerpable
```

The examples can be found in Processing under *File > Examples > Contributed Libraries > Keyed*.

Next: [Getting Started](tutorial/basics.md).
