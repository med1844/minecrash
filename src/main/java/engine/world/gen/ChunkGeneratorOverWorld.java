package engine.world.gen;

import java.util.Random;

import engine.maths.Perlin;
import engine.maths.SimplexNoise;
import engine.world.Block;
import engine.world.Chunk;
import static engine.world.TextureManager.*;

public class ChunkGeneratorOverWorld implements ChunkGenerator {
    Random rand;
    private long seed;
    private double[] heightMap;
    public static final int seaLevel = 5;
    NoiseGeneratorOctaves depthNoise;
    NoiseGeneratorOctaves mainPerlinNoise;
    NoiseGeneratorOctaves minLimitPerlinNoise;
    NoiseGeneratorOctaves maxLimitPerlinNoise;
    double[] mainNoiseRegion;
    double[] depthRegion;
    double[] minLimitRegion;
    double[] maxLimitRegion;

    public static final float coordinateScale = 64;
    public static final float heightScale = 16;
    public static final float depthNoiseScaleX = 16;
    public static final float depthNoiseScaleZ = 16;
    public static final float depthNoiseScaleExponent = 2;
    public static final float mainNoiseScaleX = 16;
    public static final float mainNoiseScaleY = 16;
    public static final float mainNoiseScaleZ = 16;
    public static final float biomeDepthOffSet = 0;

    public static final float biomeDepthWeight = 0.7f;
    public static final float biomeScaleOffset = 0;
    public static final float biomeScaleWeight = 0.7f;
    public static final float lowerLimitScale = 64;
    public static final float upperLimitScale = 64;
    public static final float stretchY = 1;
    public static final float baseSize = 1;

    public ChunkGeneratorOverWorld(long seed) {
        this.seed = seed;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        rand = new Random();
        rand.setSeed(((long) x * 341873128712L + (long) z * 132897987541L) * System.nanoTime());
        // 用于存储chunk数据
        depthNoise = new NoiseGeneratorOctaves(rand, 4);
        mainPerlinNoise = new NoiseGeneratorOctaves(rand, 4);
        minLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 4);
        maxLimitPerlinNoise = new NoiseGeneratorOctaves(rand, 4);
        Chunk chunk = new Chunk(x, z);
        // 生成只有水和石头的基本地形
        this.setBlocksInChunk(x, z, chunk);

        return chunk;
    }

    public void setBlocksInChunk(int x, int z, Chunk chunk) {
        SimplexNoise noise = new SimplexNoise(0.5, seed);
        Random rand = new Random(seed);
        for (int i = 0; i < Chunk.getX(); ++i) {
            for (int j = 0; j < Chunk.getY(); ++j) {
                for (int k = 0; k < Chunk.getZ(); ++k) {
                    double result = Math.abs(noise.get((chunk.getx() << 4) + i, j, (chunk.getz() << 4) + k)) * 5;
                    if (result <= 0.7) {
                        chunk.setBlock(AIR, i, j, k);
                    } else if (result <= 1.9) {
                        int blockID = rand.nextInt(24) + 1;
                        if (blockID == 6) blockID = 1;
                        chunk.setBlock(blockID, i, j, k);
                    } else {
                        chunk.setBlock(AIR, i, j, k);
                    }
                }
            }
        }
//        for (int i = 0; i < Chunk.getX(); ++i)
//            for (int j = 0; j < Chunk.getY(); ++j) for (int k = 0; k < Chunk.getZ(); ++k) chunk.setBlocks(AIR, i, j, k);
////        this.biomesForGeneration = this.world.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
//        // 生成地形高度图 5x5x31
//        heightMap = new double[5 * 5 * 33];
////        for (int i=0;i<heightMap.length;i++) System.out.println(heightMap[i]);
//        this.generateHeightmap(x * 4, 0, z * 4);
//        // 插值部分(看着很复杂，实际很简单，不细讲)
//        for (int i = 0; i < 4; ++i) {
//            int j = i * 5;
//            int k = (i + 1) * 5;
//
//            for (int l = 0; l < 4; ++l) {
//                int i1 = (j + l) * 33;
//                int j1 = (j + l + 1) * 33;
//                int k1 = (k + l) * 33;
//                int l1 = (k + l + 1) * 33;
//
//                for (int i2 = 0; i2 < 4; ++i2) {
//                    double d0 = 0.125D;
//                    double d1 = this.heightMap[i1 + i2];
//                    double d2 = this.heightMap[j1 + i2];
//                    double d3 = this.heightMap[k1 + i2];
//                    double d4 = this.heightMap[l1 + i2];
//                    double d5 = (this.heightMap[i1 + i2 + 1] - d1) * 0.125D;
//                    double d6 = (this.heightMap[j1 + i2 + 1] - d2) * 0.125D;
//                    double d7 = (this.heightMap[k1 + i2 + 1] - d3) * 0.125D;
//                    double d8 = (this.heightMap[l1 + i2 + 1] - d4) * 0.125D;
//
//                    for (int j2 = 0; j2 < 4; ++j2) {
//                        double d9 = 0.25D;
//                        double d10 = d1;
//                        double d11 = d2;
//                        double d12 = (d3 - d1) * 0.25D;
//                        double d13 = (d4 - d2) * 0.25D;
//
//                        for (int k2 = 0; k2 < 4; ++k2) {
//                            double d14 = 0.25D;
//                            double d16 = (d11 - d10) * 0.25D;
//                            double lvt_45_1_ = d10 - d16;
//
//                            for (int l2 = 0; l2 < 4; ++l2) {
//                                if ((lvt_45_1_ += d16) > 0.0D) {
//                                    chunk.setBlocks(GRASS, i * 4 + k2, (i2 * 4 + j2), l * 4 + l2); //should be stone
//                                } else if ((i2 * 8 + j2) < seaLevel) {
//                                    chunk.setBlocks(STONE, i * 4 + k2, (i2 * 4 + j2), l * 4 + l2);//should be ocean
//                                }
//                            }
//
//                            d10 += d12;
//                            d11 += d13;
//                        }
//
//                        d1 += d5;
//                        d2 += d6;
//                        d3 += d7;
//                        d4 += d8;
//                    }
//                }
//            }
//        }
    }

    private void generateHeightmap(int x, int y, int z) {
        // 这里一共用到了四个噪声
        this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, (double) depthNoiseScaleX, (double) depthNoiseScaleZ, (double) depthNoiseScaleExponent);
        float f = coordinateScale;
        float f1 = heightScale;
        this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, y, z, 5, 33, 5, (double) (f / mainNoiseScaleX), (double) (f1 / mainNoiseScaleY), (double) (f / mainNoiseScaleZ));
        this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, y, z, 5, 33, 5, (double) f, (double) f1, (double) f);
        this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, y, z, 5, 33, 5, (double) f, (double) f1, (double) f);
        int i = 0;
        int j = 0;
        // 对周围biome的height进行加权平均
        for (int k = 0; k < 5; ++k) {
            for (int l = 0; l < 5; ++l) {
                float f2 = 0.0F; // 平均biome影响的地表起伏
                float f3 = 0.0F; // 平均biome影响的地表高度
                float f4 = 0.0F;
                int i1 = 2;
                // 获取当前chunk的Biome（中央方块的biome）
//                Biome biome = this.biomesForGeneration[k + 2 + (l + 2) * 10];

                // 计算chunk内其他方块的biome对当前方块的影响
                for (int j1 = -2; j1 <= 2; ++j1) {
                    for (int k1 = -2; k1 <= 2; ++k1) {
//                        Biome biome1 = this.biomesForGeneration[k + j1 + 2 + (l + k1 + 2) * 10];
                        // 地表高度
//                        float f5 = biomeDepthOffSet + biome1.getBaseHeight() * biomeDepthWeight;
                        float f5 = biomeDepthOffSet + 1 * biomeDepthWeight;
                        // 地形起伏
//                        float f6 = biomeScaleOffset + biome1.getHeightVariation() *biomeScaleWeight;
                        float f6 = biomeScaleOffset + 16 * biomeScaleWeight;

                        // Biome weight用于平滑地形
                        // Biome weight为 10 / sqrt(该点到中心点的距离^2 + 0.2)
                        float biomeweight = 10.0f / (float) Math.sqrt(j1 * j1 + k1 * k1 + 0.2f);
                        float f7 = biomeweight / (f5 + 2.0F);

//                        if (biome1.getBaseHeight() > biome.getBaseHeight())
//                        {
//                            f7 /= 2.0F;
//                        }

                        f2 += f6 * f7;
                        f3 += f5 * f7;
                        f4 += f7;
                    }
                }
                f2 = f2 / f4;
                f3 = f3 / f4;
                f2 = f2 * 0.9F + 0.1F;
                f3 = (f3 * 4.0F - 1.0F) / 8.0F;
                double d7 = this.depthRegion[j] / 8000.0D;

                if (d7 < 0.0D) {
                    d7 = -d7 * 0.3D;
                }

                d7 = d7 * 3.0D - 2.0D;

                if (d7 < 0.0D) {
                    d7 = d7 / 2.0D;

                    if (d7 < -1.0D) {
                        d7 = -1.0D;
                    }

                    d7 = d7 / 1.4D;
                    d7 = d7 / 2.0D;
                } else {
                    if (d7 > 1.0D) {
                        d7 = 1.0D;
                    }

                    d7 = d7 / 8.0D;
                }

                ++j;
                // 用随机值d7使地表起伏
                double d8 = (double) f3;
                double d9 = (double) f2;
                d8 = d8 + d7 * 0.2D;
                d8 = d8 * (double) baseSize / 8.0D;
                // 最终地表y坐标
                double d0 = (double) baseSize + d8 * 4.0D;

                for (int l1 = 0; l1 < 33; ++l1) {
                    double d1 = ((double) l1 - d0) * (double) stretchY * 128.0D / 256.0D / d9;

                    if (d1 < 0.0D) {
                        d1 *= 4.0D;
                    }

                    double d2 = this.minLimitRegion[i] / (double) lowerLimitScale;
                    double d3 = this.maxLimitRegion[i] / (double) upperLimitScale;
                    double d4 = (this.mainNoiseRegion[i] / 10.0D + 1.0D) / 2.0D;
                    double d5 = Perlin.Cosine_Interpolate(d2, d3, d4) - d1;

                    if (l1 > 29) {
                        double d6 = (double) ((float) (l1 - 29) / 3.0F);
                        d5 = d5 * (1.0D - d6) + -10.0D * d6;
                    }

                    this.heightMap[i] = d5;
                    ++i;
                }
            }
        }
    }


}
