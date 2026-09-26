package ch.domizai.keyed.motion;

import ch.domizai.keyed.path.LinearPath;
import ch.domizai.keyed.path.Path;
import processing.core.PVector;

public class MotionPath implements Motion<PVector> {
    private Path pathIn, pathOut;

    public MotionPath(PVector defaultValue) {
        pathIn = new LinearPath(defaultValue, defaultValue);
        pathOut = new LinearPath(defaultValue, defaultValue);
    }

    public MotionPath(Path pathIn, Path pathOut) {
        this.pathIn = pathIn;
        this.pathOut = pathOut;
    }

    public MotionPath setPathIn(Path pathIn) {
        this.pathIn = pathIn;
        return this;
    }

    public MotionPath setPathOut(Path pathOut) {
        this.pathOut = pathOut;
        return this;
    }

    @Override
    public PVector valueIn(float d) {
        return pathIn.value(d);
    }

    @Override
    public PVector valueOut(float d) {
        return pathOut.value(d);
    }
}
