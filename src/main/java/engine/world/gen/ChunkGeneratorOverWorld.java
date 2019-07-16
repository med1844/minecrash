package engine.world.gen;

import static engine.world.TextureManager.AIR;
import static engine.world.TextureManager.STILL_WATER;
import static engine.world.TextureManager.STONE;

import java.util.Random;

import engine.maths.NoiseMath;
import engine.world.BiomeBase;
import engine.world.ChunkUtils.Chunk;

public class ChunkGeneratorOverWorld implements ChunkGenerator {
    Random rand;
    private double[] heightMap;
    public static final int seaLevel = 63;
    NoiseGeneratorPerlinOctaves depthNoise;
    NoiseGeneratorPerlinOctaves mainPerlinNoise;
    NoiseGeneratorPerlinOctaves minLimitPerlinNoise;
    NoiseGeneratorPerlinOctaves maxLimitPerlinNoise;
    NoiseGeneratorSimplexOctaves stoneSimplexNoise;
    double[] mainNoiseRegion;
    double[] depthRegion; // 2D, height-change-scale
    double[] minLimitRegion;
    double[] maxLimitRegion;
    double[] stoneRegion;

    public static final double coordinateScale = 684.412D;
    public static final double heightScale = 684.412D;
    public static final float depthNoiseScaleX = 200;
    public static final float depthNoiseScaleZ = 200;
    public static final double mainNoiseScaleX = 80;
    public static final double mainNoiseScaleY = 160;
    public static final double mainNoiseScaleZ = 80;
//    public static final float biomeDepthOffSet = 64;
//    public static final float heightVariation = 16;
//    public static final float baseHeight = 1;

//    public static final float biomeDepthWeight = 0.7f;
//    public static final float biomeScaleOffset = 0.0f;
//    public static final float biomeScaleWeight = 0.7f;
    public static final float lowerLimitScale = 512.0f;
    public static final float upperLimitScale = 512.0f;
    public static final float stretchY = 1f;
    public static final float baseSize = 8.5f; // surf average height
    public static final double persistence = 0.5;

    private BiomeBase[] biomesForGeneration;

    public ChunkGeneratorOverWorld() {
        rand = new Random();
        long seed = System.nanoTime();
//        seed=884115298253700L;945874381819300 4 16 16 16 is great
        seed = 85714926159550L;
        rand.setSeed(seed);

        int depthNoiseOctave = 4;
        int mainNoiseOctave = 16;
        int minLimitNoiseOctave = 16;
        int maxLimitNoiseOctave = 16;

        depthNoise = new NoiseGeneratorPerlinOctaves(rand, depthNoiseOctave);
        mainPerlinNoise = new NoiseGeneratorPerlinOctaves(rand, mainNoiseOctave);
        minLimitPerlinNoise = new NoiseGeneratorPerlinOctaves(rand, minLimitNoiseOctave);
        maxLimitPerlinNoise = new NoiseGeneratorPerlinOctaves(rand, maxLimitNoiseOctave);

        int stoneNoiseOctave = 4;
        stoneSimplexNoise = new NoiseGeneratorSimplexOctaves(rand, stoneNoiseOctave);

        System.out.println(seed + " " + depthNoiseOctave + " " + mainNoiseOctave + " " + minLimitNoiseOctave + " "
                + maxLimitNoiseOctave);
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        Chunk chunk = new Chunk(x, z);
        // only water and stone
        setBlocksInChunk(x, z, chunk);

        // 16*16 biomes
        // biomesForGeneration =
        // this.worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration,
        // x * 16, z * 16, 16, 16);
        biomesForGeneration = new BiomeBase[16 * 16];
        for (int i = 0; i < 16 * 16; i++)
            biomesForGeneration[i] = new BiomeBase();
        replaceBlocksForBiome(x, z, chunk, biomesForGeneration);
        return chunk;
    }

    public void setBlocksInChunk(int x, int z, Chunk chunk) {
        // init with AIR
//        for (int i = 0; i < Chunk.getX(); ++i)
//            for (int j = 0; j < Chunk.getY(); ++j)
//                for (int k = 0; k < Chunk.getZ(); ++k)
//                    chunk.setBlock(AIR, i, j, k);
//        this.biomesForGeneration = this.world.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        heightMap = new double[5 * 5 * 33];

        this.generateHeightmap(x * 4, 0, z * 4);

        for (int xHigh = 0; xHigh < 4; ++xHigh) {
            int xIndex = xHigh * 5;
            int xIndex_1 = (xHigh + 1) * 5;

            for (int zHigh = 0; zHigh < 4; ++zHigh) {
                int xzIndex = (xIndex + zHigh) * 33;
                int xz_1Index = (xIndex + zHigh + 1) * 33;
                int x_1zIndex = (xIndex_1 + zHigh) * 33;
                int x_1z_1Index = (xIndex_1 + zHigh + 1) * 33;

                for (int yHigh = 0; yHigh < 32; ++yHigh) {
                    double w0 = 0.125D;
                    double density = this.heightMap[xzIndex + yHigh];
                    double densityZ1 = this.heightMap[xz_1Index + yHigh];
                    double densityX1 = this.heightMap[x_1zIndex + yHigh];
                    double densityX1Z1 = this.heightMap[x_1z_1Index + yHigh];

                    // step
                    double densityStep = (this.heightMap[xzIndex + yHigh + 1] - density) * w0;
                    double densityZ1Step = (this.heightMap[xz_1Index + yHigh + 1] - densityZ1) * w0;
                    double densityX1Step = (this.heightMap[x_1zIndex + yHigh + 1] - densityX1) * w0;
                    double densityX1Z1Step = (this.heightMap[x_1z_1Index + yHigh + 1] - densityX1Z1) * w0;

                    for (int yLow = 0; yLow < 8; ++yLow) {
                        double w1 = 0.25D;
                        double density2 = density;
                        double density2Z1 = densityZ1;

                        // step
                        double density2Step = (densityX1 - density) * w1;
                        double density2Z1Step = (densityX1Z1 - densityZ1) * w1;

                        for (int xLow = 0; xLow < 4; ++xLow) {
                            double w3 = 0.25D;
                            double density3 = density2;
                            double density3Step = (density2Z1 - density2) * w3;

                            for (int zLow = 0; zLow < 4; ++zLow) {
                                if (density3 > 0.0D) {
                                    chunk.setBlock(STONE, xHigh * 4 + xLow, (yHigh * 8 + yLow), zHigh * 4 + zLow); // should
                                                                                                                   // be
                                                                                                                   // stone
                                } else if ((yHigh * 8 + yLow) < seaLevel) {
                                    chunk.setBlock(STILL_WATER, xHigh * 4 + xLow, (yHigh * 8 + yLow), zHigh * 4 + zLow);// should
                                                                                                                        // be
                                                                                                                        // water
                                } else {
                                    chunk.setBlock(AIR, xHigh * 4 + xLow, (yHigh * 8 + yLow), zHigh * 4 + zLow);// should
                                                                                                                // be
                                                                                                                // air
                                }

                                density3 += density3Step;
                            }

                            density2 += density2Step;
                            density2Z1 += density2Z1Step;
                        }

                        density += densityStep;
                        densityZ1 += densityZ1Step;
                        densityX1 += densityX1Step;
                        densityX1Z1 += densityX1Z1Step;
                    }
                }
            }
        }

    }

    private void generateHeightmap(int x, int y, int z) {

        // 5*5*1 noise
        this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, (double) depthNoiseScaleX,
                (double) depthNoiseScaleZ, persistence);

        double coordinateScale = this.coordinateScale;
        double heightScale = this.heightScale;
        this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, y, z, 5, 33, 5,
                (double) (coordinateScale / mainNoiseScaleX), (double) (heightScale / mainNoiseScaleY),
                (double) (coordinateScale / mainNoiseScaleZ), persistence);

        this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, y, z, 5, 33, 5,
                (double) coordinateScale, (double) heightScale, (double) coordinateScale, persistence);
        this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, y, z, 5, 33, 5,
                (double) coordinateScale, (double) heightScale, (double) coordinateScale, persistence);

        int index = 0;
        int xzIndex = 0;
        // get nearby biome's height
        for (int x1 = 0; x1 < 5; ++x1) {
            for (int z1 = 0; z1 < 5; ++z1) {
                float scale = 0.0F; // up-and-down scale
                float groundYOffset = 0.0F; // the surface's height
                float totalWeight = 0.0F;
                // the current chunk's Biome(center block's biome）
                // Biome biome = this.biomesForGeneration[k + 2 + (l + 2) * 10];

                for (int j1 = -2; j1 <= 2; ++j1) {
                    for (int k1 = -2; k1 <= 2; ++k1) {
                        // Biome biome1 = this.biomesForGeneration[k + j1 + 2 + (l + k1 + 2) * 10];
                        // up-and-down scale
                        // float curScale = biomeScaleOffset + biome1.getHeightVariation()
                        // *biomeScaleWeight;
//                        float curScale = biomeScaleOffset + heightVariation * biomeScaleWeight;
                        float curScale = 0.2f;
                        // the surface's height
                        // float curGroundYOffset = biomeDepthOffSet + biome1.getBaseHeight() *
                        // biomeDepthWeight;
//                        float curGroundYOffset = biomeDepthOffSet + baseHeight * biomeDepthWeight;
                        float curGroundYOffset = 0.1f;
                        // smooth
                        // Biome weight= 10 / sqrt((the distance to center)^2 + 0.2)
                        float biomeWeight = 10.0f / (float) Math.sqrt(j1 * j1 + k1 * k1 + 0.2f);
                        biomeWeight = biomeWeight / (curGroundYOffset + 2.0F);

//                        if (biome1.getBaseHeight() > biome.getBaseHeight())
//                        {
//                            f7 /= 2.0F;
//                        }

                        scale += curScale * biomeWeight;
                        groundYOffset += curGroundYOffset * biomeWeight;
                        totalWeight += biomeWeight;
                    }
                }
                scale = scale / totalWeight;
                groundYOffset = groundYOffset / totalWeight;
                scale = scale * 0.9F + 0.1F;
                groundYOffset = (groundYOffset * 4.0F - 1.0F) / 8.0F;

                // random=[-0.36,0.125]
                double random = this.depthRegion[xzIndex] / 8000.0D;

                if (random < 0.0D) {
                    random = -random * 0.3D;
                }

                random = random * 3.0D - 2.0D;

                if (random < 0.0D) {
                    random = random / 2.0D;

                    if (random < -1.0D) {
                        random = -1.0D;
                    }

                    random = random / 1.4D;
                    random = random / 2.0D;
                } else {
                    if (random > 1.0D) {
                        random = 1.0D;
                    }

                    random = random / 8.0D;
                }

                ++xzIndex;

                double _groundYOffset = (double) groundYOffset;
                double _scale = (double) scale;
                _groundYOffset = _groundYOffset + random * 0.2D;
                _groundYOffset = _groundYOffset * (double) baseSize / 8.0D;
                // final y
                double groundY = (double) baseSize + _groundYOffset * 4.0D;

                for (int yHigh = 0; yHigh < 33; ++yHigh) {
                    // offset<0 ---> stone , offset>0 ---> water or air
                    double offset = ((double) yHigh - groundY) * (double) stretchY * 128.0D / 256.0D / _scale;

                    if (offset < 0.0D) {
                        offset *= 4.0D;
                    }

                    double lowerLimit = this.minLimitRegion[index] / (double) lowerLimitScale;
                    double upperLimit = this.maxLimitRegion[index] / (double) upperLimitScale;
                    double tmp = (this.mainNoiseRegion[index] / 10.0D + 1.0D) / 2.0D;
                    double result;
                    if (tmp < 0)
                        result = lowerLimit - offset;
                    else if (tmp > 1)
                        result = upperLimit - offset;
                    else
                        result = NoiseMath.LinearInterpolateImproved(lowerLimit, upperLimit, tmp) - offset;

                    if (yHigh > 29) {
                        double tmp2 = (double) ((float) (yHigh - 29) / 3.0F);
                        result = result * (1.0D - tmp2) + -10.0D * tmp2;
                    }

                    this.heightMap[index] = result;
                    ++index;
                }
            }
        }

    }

    public void replaceBlocksForBiome(int chunkX, int chunkZ, Chunk chunk, BiomeBase[] biomes) {

        double coordinateScale = 0.03125D;
        // 生成16*16的噪声
        stoneRegion = stoneSimplexNoise.generateNoiseOctaves(stoneRegion, (double) (chunkX * 16),
                (double) (chunkZ * 16), 16, 16, coordinateScale * 2.0D, coordinateScale * 2.0D, 1.0D);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                BiomeBase biomebase = biomes[z + x * 16];
                biomebase.genBlocks(rand, chunk, chunkX * 16 + x, chunkZ * 16 + z, stoneRegion[z + x * 16]);
            }
        }
    }

}
