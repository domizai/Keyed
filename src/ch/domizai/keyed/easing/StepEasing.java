package ch.domizai.keyed.easing;

public class StepEasing implements Easing {
    private float x = 0.5f;
    
    public StepEasing() {
    }

    public StepEasing(float x) {
        this.x = x;
    }

    @Override
    public float apply(float d) {
        return d < x ? 0 : 1;
    }
}
