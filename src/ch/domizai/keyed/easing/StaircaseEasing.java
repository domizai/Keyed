package ch.domizai.keyed.easing;

public class StaircaseEasing implements Easing {
    private int steps = 1;
    
    public StaircaseEasing(int steps) {
        this.steps = steps;
    }

    @Override
    public float apply(float d) {
        return (float) Math.round(d * steps) / steps;
    }
}
