package engine.world.gen;

import java.util.Random;

public class NoiseGeneratorSimplex {
    // 梯度向量
    private static int[][] xy2D = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    // √3
    public static final double sqrt3 = Math.sqrt(3.0D);
    // 0~255的随机排列
    private int[] permutation;
    // 坐标偏移量
    public double dx;
    public double dy;
    public double dz;
    // (√3 - 1) / 2，把二维空间下的单形网格变形成新网格公式中的K1
    private static final double k1 = 0.5D * (sqrt3 - 1.0D);
    // (3 - √3) / 6，把新网格变回原来空间公式中的K2
    private static final double k2 = (3.0D - sqrt3) / 6.0D;

    public NoiseGeneratorSimplex() {
        this(new Random());
    }

    // 同NoiseGeneratorImproved，略
    public NoiseGeneratorSimplex(Random rand) {
        this.permutation = new int[512];
        this.dx = rand.nextDouble() * 256.0D;
        this.dy = rand.nextDouble() * 256.0D;
        this.dz = rand.nextDouble() * 256.0D;

        for (int i = 0; i < 256; this.permutation[i] = i++) {
            ;
        }

        for (int l = 0; l < 256; ++l) {
            int j = rand.nextInt(256 - l) + l;
            int k = this.permutation[l];
            this.permutation[l] = this.permutation[j];
            this.permutation[j] = k;
            this.permutation[l + 256] = this.permutation[l];
        }
    }

    // 返回不大于num的最大整数
    private static int floor(double num) {
        return num > 0.0D ? (int) num : (int) num - 1;
    }

    // 返回与二维向量点乘的结果，参数：两个向量
    private static double dot(int[] vector, double xWeight, double zWeight) {
        return (double) vector[0] * xWeight
                + (double) vector[1] * zWeight;
    }

    // 计算一个点的噪声值
    public double getNoise(double x, double z) {
        // (√3 - 1) / 2，把二维空间下的单形网格变形成新网格公式中的K1
        double K1 = 0.5D * (sqrt3 - 1.0D);
        // 把二维空间下的三角形网格变形成新网格
        double offset = (x + z) * K1;
        // func_151607_a为向下取整，newXZ为该点在新网格的超立方体坐标
        int newX = floor(x + offset);
        int newZ = floor(z + offset);

        // (3 - √3) / 6，把新网格变回原来空间公式中的K2
        double K2 = (3.0D - sqrt3) / 6.0D;
        // 把新网格坐标变回三角形网格的坐标
        double offset2 = (double) (newX + newZ) * K2;
        // 点所在三角形左下角的坐标
        double oldTriangleX = (double) newX - offset2;
        double oldTriangleZ = (double) newZ - offset2;

        // 点相对于所在三角形的坐标
        double dx1 = x - oldTriangleX;
        double dx2 = z - oldTriangleZ;

        // 根据点在哪个三角形内，需要计算第二个顶点索引
        int vec2XIndexOffset;
        int vec2ZIndexOffset;

        if (dx1 > dx2) {
            // 在右下角的三角形内
            vec2XIndexOffset = 1;
            vec2ZIndexOffset = 0;
        } else {
            // 在左上角的三角形内
            vec2XIndexOffset = 0;
            vec2ZIndexOffset = 1;
        }

        int xIndex = newX % 256;
        int zIndex = newZ % 256;
        // 三个顶点对应的梯度向量索引
        int vecIndex1 = this.permutation[xIndex + this.permutation[zIndex]] % 12;
        int vecIndex2 = this.permutation[xIndex + vec2XIndexOffset + this.permutation[zIndex + vec2ZIndexOffset]] % 12;
        int vecIndex3 = this.permutation[xIndex + 1 + this.permutation[zIndex + 1]] % 12;

        // r^2 - dist^2
        double tmp1 = 0.5D - dx1 * dx1 - dx2 * dx2;
        double res1;

        if (tmp1 < 0.0D) {
            // 到该顶点距离太远，贡献为0
            res1 = 0.0D;
        } else {
            tmp1 = tmp1 * tmp1;
            // 该顶点对结果的贡献度：(r^2 − dist^2)^4 * dot(dist, grad)
            res1 = tmp1 * tmp1 * dot(xy2D[vecIndex1], dx1, dx2);
        }

        dx2 = dx1 - (double) vec2XIndexOffset + K2;
        double dz2 = dx2 - (double) vec2ZIndexOffset + K2;
        double tmp2 = 0.5D - dx2 * dx2 - dz2 * dz2;
        double res2;

        if (tmp2 < 0.0D) {
            res2 = 0.0D;
        } else {
            tmp2 = tmp2 * tmp2;
            res2 = tmp2 * tmp2 * dot(xy2D[vecIndex2], dx2, dz2);
        }

        double dx3 = dx1 - 1.0D + 2.0D * K2;
        double dz3 = dx2 - 1.0D + 2.0D * K2;
        double tmp3 = 0.5D - dx3 * dx3 - dz3 * dz3;
        double res3;

        if (tmp3 < 0.0D) {
            res3 = 0.0D;
        } else {
            tmp3 = tmp3 * tmp3;
            res3 = tmp3 * tmp3 * dot(xy2D[vecIndex3], dx3, dz3);
        }

        return 70.0D * (res1 + res2 + res3);
    }

    // 生成噪声，这个noiseScale和振幅成正比
    public void generateNoiseSimplex(double[] result, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double noiseScale) {
        int resultIndex = 0;

        for (int _z = 0; _z < zSize; ++_z) {
            double z = (zOffset + (double) _z) * zScale + this.dy;

            for (int _x = 0; _x < xSize; ++_x) {
                double x = (xOffset + (double) _x) * xScale + this.dx;

                // 把二维空间下的三角形网格变形成新网格
                double offset = (x + z) * k1;
                // func_151607_a为向下取整，newXZ为该点在新网格的超立方体坐标
                int newX = floor(x + offset);
                int newZ = floor(z + offset);

                // 把新网格坐标变回三角形网格的坐标
                double offset2 = (double) (newX + newZ) * k2;
                // 点所在三角形左下角的坐标
                double oldTriangleX = (double) newX - offset2;
                double oldTriangleY = (double) newZ - offset2;

                // 点相对于所在三角形的坐标
                double dx1 = x - oldTriangleX;
                double dz1 = z - oldTriangleY;

                // 根据点在哪个三角形内，需要计算第二个顶点索引
                int vec2XIndexOffset;
                int vec2ZIndexOffset;

                if (dx1 > dz1) {
                    // 在右下角的三角形内
                    vec2XIndexOffset = 1;
                    vec2ZIndexOffset = 0;
                } else {
                    // 在左上角的三角形内
                    vec2XIndexOffset = 0;
                    vec2ZIndexOffset = 1;
                }

                int xIndex = newX % 256;
                int zIndex = newZ % 256;
                // 三个顶点对应的梯度向量索引
                int vecIndex1 = this.permutation[xIndex + this.permutation[zIndex]] % 12;
                int vecIndex2 = this.permutation[xIndex + vec2XIndexOffset + this.permutation[zIndex + vec2ZIndexOffset]] % 12;
                int vecIndex3 = this.permutation[xIndex + 1 + this.permutation[zIndex + 1]] % 12;

                // r^2 - dist^2
                double tmp1 = 0.5D - dx1 * dx1 - dz1 * dz1;
                double res1;

                if (tmp1 < 0.0D) {
                    // 到该顶点距离太远，贡献为0
                    res1 = 0.0D;
                } else {
                    tmp1 = tmp1 * tmp1;
                    // 该顶点对结果的贡献度：(r^2 − dist^2)^4 * dot(dist, grad)
                    res1 = tmp1 * tmp1 * dot(xy2D[vecIndex1], dx1, dz1);
                }

                double dx2 = dx1 - (double) vec2XIndexOffset + k2;
                double dz2 = dz1 - (double) vec2ZIndexOffset + k2;
                double tmp2 = 0.5D - dx2 * dx2 - dz2 * dz2;
                double res2;

                if (tmp2 < 0.0D) {
                    res2 = 0.0D;
                } else {
                    tmp2 = tmp2 * tmp2;
                    res2 = tmp2 * tmp2 * dot(xy2D[vecIndex2], dx2, dz2);
                }

                double dx3 = dx1 - 1.0D + 2.0D * k2;
                double dz3 = dz1 - 1.0D + 2.0D * k2;
                double tmp3 = 0.5D - dx3 * dx3 - dz3 * dz3;
                double res3;

                if (tmp3 < 0.0D) {
                    res3 = 0.0D;
                } else {
                    tmp3 = tmp3 * tmp3;
                    res3 = tmp3 * tmp3 * dot(xy2D[vecIndex3], dx3, dz3);
                }

                result[resultIndex++] += 70.0D * (res1 + res2 + res3) * noiseScale;
            }
        }
    }
}
