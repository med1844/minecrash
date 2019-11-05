package io.github.medioqrity.engine.graphics;

public class Fog {

    private float density;

    public Fog() {
        density = 0.005f;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

}
