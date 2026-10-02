package ch.domizai.keyed;

import ch.domizai.keyed.lerp.PVectorLerp;
import processing.core.PVector;

public class KVector extends KeyedBase<PVector, KVector> {

    public KVector() {
        super(new PVectorLerp(), new PVector());
    }

    public KVector(PVector vec) {
        super(new PVectorLerp(), vec);
    }
    
    public KVector(float x, float y) {
        super(new PVectorLerp(), new PVector(x, y));
    }
    
    public KVector(float x, float y, float z) {
        super(new PVectorLerp(), new PVector(x, y, z));
    }

    @Override
    protected KVector self() {
        return this;
    }
}
