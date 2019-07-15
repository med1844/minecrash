package engine.world;

import engine.world.gen.ChunkGeneratorOverWorld;

import java.util.Random;

import static engine.world.TextureManager.*;

public class BiomeBase {
    private int topBlockID;
    private int fillerBlockID;

    public BiomeBase() {
        topBlockID = GRASS;
        fillerBlockID = STONE;
    }

    public void genBlocks(Random rand, Chunk chunk, int x, int z, double noise) {
        int seaLevel = ChunkGeneratorOverWorld.seaLevel;

        int topBlockID = this.topBlockID; // grass or others
        int fillerBlockID = this.fillerBlockID; // stone or others
        // res blocks to fill
        int res = -1;

        // blocks needed to fill
        int cnt = (int) (noise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int xLow = x & 15;
        int zLow = z & 15;

        for (int y = 255; y >= 0; --y) {
            if (y <= rand.nextInt(4)) {
                // y <= 4 BEDROCK
                chunk.setBlock(BEDROCK, zLow, y, xLow);
            } else {
                Block curBlock = chunk.getBlock(zLow, y, xLow);

                if (curBlock.getType() == AIR) {
                    res = -1;
                } else if (curBlock.getType() == STONE) {
                    if (res == -1) {
                        // y is between air and stone
                        if (cnt <= 0) {
                            topBlockID = AIR;
                            fillerBlockID = STONE;
                        } else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                            // near seaLevel
                            topBlockID = this.topBlockID;
                            fillerBlockID = this.fillerBlockID;
                        } // else under the seaLevel, nothing changed

                        // under the seaLevel and don't need to change
                        if (y < seaLevel && (topBlockID == AIR)) {
                            // ICE or WATER
//                            if (this.getFloatTemperature(pos.set(x, y, z)) < 0.15F) {
//                                topBlock.set(ICE);
//                            } else {
//                                topBlock.set(WATER);
//                            }
                            topBlockID = STILL_WATER;
                        }

                        // fill the top
                        res = cnt;

                        if (y >= seaLevel - 1) {
                            // above seaLevel
                            // fill topBlock
                            chunk.setBlock(topBlockID, zLow, y, xLow);
                        } else if (y < seaLevel - 7 - cnt) {
                            // ocean bottom
                            topBlockID = GRAVEL;
                            // fill with stone
                            fillerBlockID = STONE;
                            // top with gravel
                            chunk.setBlock(GRAVEL, zLow, y, xLow);
                        } else {
                            // below seaLevel but near seaLevel
                            // fill with fillerBlock
                            chunk.setBlock(fillerBlockID, zLow, y, xLow);
                        }
                    } else if (res > 0) {
                        //filling
                        --res;
                        //fill with fillerBlock
                        chunk.setBlock(fillerBlockID, zLow, y, xLow);

                        // if fill sand before, then fill with sandstone
                        if (res == 0 && fillerBlockID == SAND) {
                            res = rand.nextInt(4) + Math.max(0, y - 63);
                            fillerBlockID = SANDSTONE;
                        }
                    } //res=0 finish
                }
            }
        }
    }
}
