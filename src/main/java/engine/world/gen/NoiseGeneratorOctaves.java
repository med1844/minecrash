package engine.world.gen;

import java.util.Random;

//分形叠加多个NoiseGeneratorPerlinBase

public class NoiseGeneratorOctaves {
    /** Collection of noise generation functions.  Output is combined to produce different octaves of noise. */
    private NoiseGeneratorPerlinBase[] generatorCollection;
    private int octaves;

    public NoiseGeneratorOctaves(Random rand, int octaveCount)
    {
        this.octaves = octaveCount;
        this.generatorCollection = new NoiseGeneratorPerlinBase[octaveCount];

        for (int i = 0; i < octaveCount; ++i)
        {
            this.generatorCollection[i] = new NoiseGeneratorPerlinBase(rand);
        }
    }

    /**
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    public double[] generateNoiseOctaves(double[] result, int x, int y, int z, int xArraySize, int yArraySize, int zArraySize, double xScale, double yScale, double zScale)
    {
        if (result == null)
        {
            result = new double[xArraySize * yArraySize * zArraySize];
        }
        else
        {
            // 结果清0，因为后面会叠加
            for (int i = 0; i < result.length; ++i)
            {
                result[i] = 0.0D;
            }
        }

        // 控制倍频函数的频率、幅度
        double scale = 1.0D;

        // 遍历倍频函数
        for (int i = 0; i < this.octaves; ++i)
        {
            double x2 = (double)x * scale * xScale;
            double y2 = (double)y * scale * yScale;
            double z2 = (double)z * scale * zScale;

            // 这部分大概是防止溢出的
            // x2Floor为不大于x2的最大整数
            long x2Floor = (long)Math.floor(x2);
            long z2Floor = (long)Math.floor(z2);
            x2 = x2 - (double)x2Floor;
            z2 = z2 - (double)z2Floor;
            x2Floor = x2Floor % 0x1000000L;
            z2Floor = z2Floor % 0x1000000L;
            x2 = x2 + (double)x2Floor;
            z2 = z2 + (double)z2Floor;

            // 叠加
            this.generatorCollection[i].populateNoiseArray(result, x2, y2, z2, xArraySize, yArraySize, zArraySize, xScale * scale, yScale * scale, zScale * scale, scale);
            // 频率减半，幅度加倍
            scale /= 2.0D;
        }

        return result;
    }

    /**
     * Bouncer function to the main one with some default arguments.
     */
    public double[] generateNoiseOctaves(double[] result, int x, int z, int xArraySize, int zArraySize, double xScale, double zScale, double p_76305_10_)
    {
        return this.generateNoiseOctaves(result, x, 10, z, xArraySize, 1, zArraySize, xScale, 1.0D, zScale);
    }
}
