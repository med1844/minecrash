package io.github.medioqrity.engine.world.gen;

import java.util.Random;

public class NoiseGeneratorSimplexOctaves {
    private NoiseGeneratorSimplex[] generatorCollection;
    private int octaves;

    // 同NoiseGeneratorOctaves
    public NoiseGeneratorSimplexOctaves(Random rand, int octaves) {
        this.octaves = octaves;
        this.generatorCollection = new NoiseGeneratorSimplex[octaves];

        for (int i = 0; i < octaves; ++i) {
            this.generatorCollection[i] = new NoiseGeneratorSimplex(rand);
        }
    }

    public double getNoise(double x, double y) {
        double d0 = 0.0D;
        double d1 = 1.0D;

        for (int i = 0; i < this.octaves; ++i) {
            d0 += this.generatorCollection[i].getNoise(x * d1, y * d1) / d1;
            d1 /= 2.0D;
        }

        return d0;
    }

    /**
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    // 生成噪声，默认振幅系数0.5
    public double[] generateNoiseOctaves(double[] result, double x, double z, int xArraySize, int zArraySize, double xScale, double zScale, double scale) {
        return this.generateNoiseOctaves(result, x, z, xArraySize, zArraySize, xScale, zScale, scale, 0.5D);
    }

    // 生成噪声，_freqScale为每次迭代频率系数(一般取1，不变)
    // _noiseScale为每次迭代振幅系数，也会影响频率(一般取0.5，振幅加倍，频率减半)
    public double[] generateNoiseOctaves(double[] result, double x, double z, int xSize, int zSize, double xScale, double zScale, double _freqScale, double _noiseScale) {
        if (result != null && result.length >= xSize * zSize) {
            for (int i = 0; i < result.length; ++i) {
                result[i] = 0.0D;
            }
        } else {
            result = new double[xSize * zSize];
        }

        // 和振幅成反比
        double noiseScale = 1.0D;
        // 和频率成正比
        double freqScale = 1.0D;

        for (int i = 0; i < this.octaves; ++i) {
            this.generatorCollection[i].generateNoiseSimplex(result, x, z, xSize, zSize, xScale * freqScale * noiseScale, zScale * freqScale * noiseScale, 0.55D / noiseScale);
            freqScale *= _freqScale;
            noiseScale *= _noiseScale;
        }

        return result;
    }
}
