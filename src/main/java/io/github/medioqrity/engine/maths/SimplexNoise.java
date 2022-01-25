package io.github.medioqrity.engine.maths;

import java.util.Random;

public class SimplexNoise {

    private long seed;
    private double persistence;
    private SimplexNoiseOctave[] octaves;
    private double[] freqs;
    private double[] amps;
    private final int OCTAVE_COUNT = 1 << 3;

    public SimplexNoise(double persistence, long seed) {
        this.persistence = persistence;
        this.seed = seed;

        octaves = new SimplexNoiseOctave[OCTAVE_COUNT];
        freqs = new double[OCTAVE_COUNT];
        amps = new double[OCTAVE_COUNT];

        Random rand = new Random(seed);

        for (int i = 0; i < OCTAVE_COUNT; ++i) {
            octaves[i] = new SimplexNoiseOctave(rand.nextLong());
            freqs[i] = 1 << i;
            amps[i] = Math.pow(persistence, octaves.length - i);
        }
    }

    public double get(int x, int y) {
        double result = 0;
        for (int i = 0; i < OCTAVE_COUNT; ++i) result += octaves[i].noise(x / freqs[i], y / freqs[i]) * amps[i];
        return result;
    }

    public double get(int x, int y, int z) {
        double result = 0;
        for (int i = 0; i < OCTAVE_COUNT; ++i) result += octaves[i].noise(x / freqs[i], y / freqs[i], z / freqs[i]) * amps[i];
        return result;
    }
}
