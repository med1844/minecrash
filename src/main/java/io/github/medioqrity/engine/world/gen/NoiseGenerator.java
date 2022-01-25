package io.github.medioqrity.engine.world.gen;

public abstract class NoiseGenerator {
    protected int[] permutations;

    public final double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
}
