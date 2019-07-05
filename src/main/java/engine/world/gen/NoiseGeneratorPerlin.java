package engine.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin {
    private NoiseGeneratorSimplex[] field_151603_a;
    private int field_151602_b;

    // 同NoiseGeneratorOctaves，略
    public NoiseGeneratorPerlin(Random p_i45470_1_, int p_i45470_2_)
    {
        this.field_151602_b = p_i45470_2_;
        this.field_151603_a = new NoiseGeneratorSimplex[p_i45470_2_];

        for (int i = 0; i < p_i45470_2_; ++i)
        {
            this.field_151603_a[i] = new NoiseGeneratorSimplex(p_i45470_1_);
        }
    }

    public double func_151601_a(double p_151601_1_, double p_151601_3_)
    {
        double d0 = 0.0D;
        double d1 = 1.0D;

        for (int i = 0; i < this.field_151602_b; ++i)
        {
            d0 += this.field_151603_a[i].func_151605_a(p_151601_1_ * d1, p_151601_3_ * d1) / d1;
            d1 /= 2.0D;
        }

        return d0;
    }

    // 生成噪声，默认振幅系数0.5
    public double[] func_151599_a(double[] p_151599_1_, double p_151599_2_, double p_151599_4_, int p_151599_6_, int p_151599_7_, double p_151599_8_, double p_151599_10_, double p_151599_12_)
    {
        return this.func_151600_a(p_151599_1_, p_151599_2_, p_151599_4_, p_151599_6_, p_151599_7_, p_151599_8_, p_151599_10_, p_151599_12_, 0.5D);
    }

    // 生成噪声，_freqScale为每次迭代频率系数(一般取1，不变)
    // _noiseScale为每次迭代振幅系数，也会影响频率(一般取0.5，振幅加倍，频率减半)
    public double[] func_151600_a(double[] result, double x, double z, int xSize, int zSize, double xScale, double zScale, double _freqScale, double _noiseScale)
    {
        if (result != null && result.length >= xSize * zSize)
        {
            // 结果清0，因为后面会叠加
            for (int i = 0; i < result.length; ++i)
            {
                result[i] = 0.0D;
            }
        }
        else
        {
            result = new double[xSize * zSize];
        }

        // 和振幅成反比
        double noiseScale = 1.0D;
        // 和频率成正比
        double freqScale = 1.0D;

        for (int i = 0; i < this.field_151602_b; ++i)
        {
            this.field_151603_a[i].func_151606_a(result, x, z, xSize, zSize, xScale * freqScale * noiseScale, zScale * freqScale * noiseScale, 0.55D / noiseScale);
            freqScale *= _freqScale;
            noiseScale *= _noiseScale;
        }

        return result;
    }
}
