package engine.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlinBase {
    // 0~255的随机排列
    private int[] permutations;
    // 坐标偏移量
    public double xCoord;
    public double yCoord;
    public double zCoord;
    // 三维梯度向量，去掉y维度后和二维的一样
    private static final double[] field_152381_e = new double[] {1.0D, -1.0D,  1.0D, -1.0D, 1.0D, -1.0D,  1.0D, -1.0D, 0.0D,  0.0D,  0.0D,  0.0D, 1.0D,  0.0D, -1.0D,  0.0D};
    private static final double[] field_152382_f = new double[] {1.0D,  1.0D, -1.0D, -1.0D, 0.0D,  0.0D,  0.0D,  0.0D, 1.0D, -1.0D,  1.0D, -1.0D, 1.0D, -1.0D,  1.0D, -1.0D};
    private static final double[] field_152383_g = new double[] {0.0D,  0.0D,  0.0D,  0.0D, 1.0D,  1.0D, -1.0D, -1.0D, 1.0D,  1.0D, -1.0D, -1.0D, 0.0D,  1.0D,  0.0D, -1.0D};
    // 二维梯度向量
    private static final double[] field_152384_h = new double[] {1.0D, -1.0D,  1.0D, -1.0D, 1.0D, -1.0D,  1.0D, -1.0D, 0.0D,  0.0D,  0.0D,  0.0D, 1.0D,  0.0D, -1.0D,  0.0D};
    private static final double[] field_152385_i = new double[] {0.0D,  0.0D,  0.0D,  0.0D, 1.0D,  1.0D, -1.0D, -1.0D, 1.0D,  1.0D, -1.0D, -1.0D, 0.0D,  1.0D,  0.0D, -1.0D};

    public NoiseGeneratorPerlinBase()
    {
        this(new Random());
    }

    public NoiseGeneratorPerlinBase(Random rand)
    {
        this.permutations = new int[512];
        this.xCoord = rand.nextDouble() * 256.0D;
        this.yCoord = rand.nextDouble() * 256.0D;
        this.zCoord = rand.nextDouble() * 256.0D;

        // 生成0~255的随机排列

        for (int i = 0; i < 256; ++i)
        {
            this.permutations[i] = i;
        }

        for (int i = 0; i < 256; ++i)
        {
            // 从i~255中选j
            int j = rand.nextInt(256 - i) + i;
            // 把i和j交换
            int t = this.permutations[i];
            this.permutations[i] = this.permutations[j];
            this.permutations[j] = t;
            // 形成一个周期
            this.permutations[i + 256] = this.permutations[i];
        }
    }

    // 在a与b间线性插值
    public final double lerp(double t, double a, double b)
    {
        return a + t * (b - a);
    }

    // 返回与二维梯度向量点乘的结果，参数：梯度向量索引, 向量（其实就是权重）
    public final double func_76309_a(int index, double xWeight, double zWeight)
    {
        int i = index % 16;
        return   field_152384_h[i] * xWeight
               + field_152385_i[i] * zWeight;
    }

    // 返回与三维梯度向量点乘的结果，参数：梯度向量索引, 向量（其实就是权重）
    public final double grad(int index, double xWeight, double yWeight, double zWeight)
    {
        int i = index % 16;
        return   field_152381_e[i] * xWeight 
               + field_152382_f[i] * yWeight
               + field_152383_g[i] * zWeight;
    }

    /**
     * pars: noiseArray , xOffset , yOffset , zOffset , xSize , ySize , zSize , xScale, yScale , zScale , noiseScale.
     * noiseArray should be xSize*ySize*zSize in size
     */
    // 这个noiseScale和振幅成反比...
    public void populateNoiseArray(double[] result, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double noiseScale)
    {
        if (ySize == 1) // 二维
        {
            int resultIndex = 0;
            // 结果缩放系数，和noiseScale成反比
            double noiseRatio = 1.0D / noiseScale;

            for (int _x = 0; _x < xSize; ++_x)
            {
                // 经过偏移和缩放的x
                double x = xOffset + (double)_x * xScale + this.xCoord;
                // 不大于x的最大整数
                int xFloor = (int)x;
                if (x < (double)xFloor)
                {
                    --xFloor;
                }

                int xIndex = xFloor % 256;
                // 此时x为晶格内的坐标[0, 1)
                x = x - (double)xFloor;
                // 缓和曲线s(t) = 6t^5 - 15t^4 + 10t^3
                double sx = x * x * x * (x * (x * 6.0D - 15.0D) + 10.0D);

                for (int _z = 0; _z < zSize; ++_z)
                {
                    // 经过偏移和缩放的z
                    double z = zOffset + (double)_z * zScale + this.zCoord;
                    // 不大于z的最大整数
                    int zFloor = (int)z;
                    if (z < (double)zFloor)
                    {
                        --zFloor;
                    }

                    int zIndex = zFloor % 256;
                    // 此时z为晶格内的坐标[0, 1)
                    z = z - (double)zFloor;
                    // 缓和曲线s(t) = 6t^5 - 15t^4 + 10t^3
                    double sz = z * z * z * (z * (z * 6.0D - 15.0D) + 10.0D);

                    // 取梯度向量G=G[(i+P[j])mod n]
                    // 左上角梯度向量索引
                    int vecIndex1 = this.permutations[this.permutations[xIndex]] + zIndex;
                    // 右上角梯度向量索引
                    int vecIndex2 = this.permutations[this.permutations[xIndex + 1]] + zIndex;

                    // x方向上点乘（加权），结果用缓和曲线插值
                    double xRes1 = this.lerp(sx,
                                             this.func_76309_a(this.permutations[vecIndex1],          x ,       z),
                                             this.grad(        this.permutations[vecIndex2], -(1.0D - x), 0.0D, z));
                    double xRes2 = this.lerp(sx,
                                             this.grad(this.permutations[vecIndex1 + 1],          x , 0.0D, -(1.0D - z)),
                                             this.grad(this.permutations[vecIndex2 + 1], -(1.0D - x), 0.0D, -(1.0D - z)));
                    // 两个x方向的结果在z方向用缓和曲线插值
                    double res = this.lerp(sz, xRes1, xRes2);

                    result[resultIndex++] += res * noiseRatio;
                }
            }
        }
        else // 三维，参考上面，懒得写注释和反混淆了...
        {
            int resultIndex = 0;
            double noiseRatio = 1.0D / noiseScale;
            int lastYIndex = -1;

            for (int _x = 0; _x < xSize; ++_x)
            {
                double x = xOffset + (double)_x * xScale + this.xCoord;
                int xFloor = (int)x;

                if (x < (double)xFloor)
                {
                    --xFloor;
                }

                int xIndex = xFloor % 256;
                x = x - (double)xFloor;
                double sx = x * x * x * (x * (x * 6.0D - 15.0D) + 10.0D);

                for (int _z = 0; _z < zSize; ++_z)
                {
                    double z = zOffset + (double)_z * zScale + this.zCoord;
                    int zFloor = (int)z;

                    if (z < (double)zFloor)
                    {
                        --zFloor;
                    }

                    int zIndex = zFloor % 256;
                    z = z - (double)zFloor;
                    double sz = z * z * z * (z * (z * 6.0D - 15.0D) + 10.0D);

                    double xRes1, xRes2, xRes3, xRes4;

                    for (int _y = 0; _y < ySize; ++_y)
                    {
                        double y = yOffset + (double)_y * yScale + this.yCoord;
                        int yFloor = (int)y;

                        if (y < (double)yFloor)
                        {
                            --yFloor;
                        }

                        int yIndex = yFloor % 256;
                        y = y - (double)yFloor;
                        double sy = y * y * y * (y * (y * 6.0D - 15.0D) + 10.0D);

                        if (_y == 0 || yIndex != lastYIndex)
                        {
                            lastYIndex = yIndex;
                        }
                            int l = this.permutations[xIndex] + yIndex;
                            int i1 = this.permutations[l] + zIndex;
                            int j1 = this.permutations[l + 1] + zIndex;
                            int k1 = this.permutations[xIndex + 1] + yIndex;
                            int l1 = this.permutations[k1] + zIndex;
                            int i2 = this.permutations[k1 + 1] + zIndex;
                            xRes1 = this.lerp(sx, this.grad(this.permutations[i1], x, y, z), this.grad(this.permutations[l1], x - 1.0D, y, z));
                            xRes2 = this.lerp(sx, this.grad(this.permutations[j1], x, y - 1.0D, z), this.grad(this.permutations[i2], x - 1.0D, y - 1.0D, z));
                            xRes3 = this.lerp(sx, this.grad(this.permutations[i1 + 1], x, y, z - 1.0D), this.grad(this.permutations[l1 + 1], x - 1.0D, y, z - 1.0D));
                            xRes4 = this.lerp(sx, this.grad(this.permutations[j1 + 1], x, y - 1.0D, z - 1.0D), this.grad(this.permutations[i2 + 1], x - 1.0D, y - 1.0D, z - 1.0D));
                        
                        double yRes1 = this.lerp(sy, xRes1, xRes2);
                        double yRes2 = this.lerp(sy, xRes3, xRes4);
                        double res = this.lerp(sz, yRes1, yRes2);

                        result[resultIndex++] += res * noiseRatio;
                    }
                }
            }
        }
    }
}
