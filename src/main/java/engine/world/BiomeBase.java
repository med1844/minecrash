package engine.world;

import java.util.Random;
import engine.world.ChunkUtils.Chunk;
import engine.world.gen.ChunkGeneratorOverWorld;

import static engine.world.TextureManager.*;

public class BiomeBase {
    private Block topBlock;
    private Block fillerBlock;
    private int minTreeHeight = 8;

    public BiomeBase() {
        topBlock = new Block(GRASS, 0, 0, 0);
        fillerBlock = new Block(DIRT, 0, 0, 0);
    }

    public void genBlocks(Random rand, Chunk chunk, int x, int z, double noise) {
        int seaLevel = ChunkGeneratorOverWorld.seaLevel;

        Block topBlock = this.topBlock; // grass or others
        Block fillerBlock = this.fillerBlock; // stone or others
        // res blocks to fill
        int res = -1;

        // blocks needed to fill
        int cnt = (int) (noise / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        int xLow = x % 16;
        int zLow = z % 16;
//        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int y = 255; y >= 0; --y) {
            if (y <= rand.nextInt(4)) {
                // y <= 4 COBBLESTONE
                chunk.setBlock(BEDROCK, zLow, y, xLow);
            } else {
                Block curBlock = chunk.getBlock(zLow, y, xLow);

                if (curBlock.getType() == AIR) {
                    res = -1;
                } else if (curBlock.getType() == STONE) {
                    if (res == -1) {
                        // y is between air and stone
                        if (cnt <= 0) {
                            topBlock.set(AIR);
                            fillerBlock.set(STONE);
                        } else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                            // near seaLevel
                            topBlock = this.topBlock;
                            fillerBlock = this.fillerBlock;
                        } // else under the seaLevel, nothing changed

                        // under the seaLevel and don't need to change
                        if (y < seaLevel && (topBlock == null || topBlock.getBlockID() == AIR)) {
                            // ICE or WATER
//                            if (this.getFloatTemperature(pos.set(x, y, z)) < 0.15F) {
//                                topBlock.set(ICE);
//                            } else {
//                                topBlock.set(WATER);
//                            }
                            topBlock.set(STILL_WATER);
                        }

                        // fill the top
                        res = cnt;

                        if (y >= seaLevel - 1) {
                            // above seaLevel
                            // fill topBlock
                            chunk.setBlock(topBlock.getBlockID(), zLow, y, xLow);
                            genTree(rand, chunk, xLow, y, zLow);
                        } else if (y < seaLevel - 7 - cnt) {
                            // ocean bottom
                            topBlock.set(GRAVEL);
                            // fill with stone
                            fillerBlock.set(STONE);
                            ;
                            // top with gravel
                            chunk.setBlock(GRAVEL, zLow, y, xLow);
                        } else {
                            // beblow seaLevel but near seaLevel
                            // topBlock is null，so fill with fillerBlock
                            chunk.setBlock(fillerBlock.getBlockID(), zLow, y, xLow);
                        }
                    } else if (res > 0) {
                        // filling
                        --res;
                        // fill with fillerBlock
                        chunk.setBlock(fillerBlock.getBlockID(), zLow, y, xLow);

                        // if fill sand before, then fill with sandstone
                        if (res == 0 && fillerBlock.getBlockID() == SAND) {
                            res = rand.nextInt(4) + Math.max(0, y - 63);
                            fillerBlock.set(SANDSTONE);
                        }
                    } // res=0 finish
                }
            }
        }
    }

    public void genTree(Random rand, Chunk chunk, int xLow, int y, int zLow) {
        if (rand.nextInt(10)>=1) return;
        int height = rand.nextInt(3) + minTreeHeight;
        if (chunk.getBlock(xLow, y, zLow).getBlockID() == GRASS || chunk.getBlock(xLow, y, zLow).getBlockID() == DIRT) {
            y++;
            boolean isReplaceable = true;
            if (y >= 1 && y + height + 1 < Chunk.getY()) {
                for (int h = y; h <= y + 1 + height; ++h) {
                    int xzSize = 1;

                    // bottom
                    if (h == y) {
                        xzSize = 0;
                    }

                    // top
                    if (h >= y + height - 1) {
                        xzSize = 2;
                    }
                    // check replaceable
                    int tx = xLow;
                    int ty = y;
                    int tz = zLow;
                    for (int i = xLow - xzSize; i <= xLow + xzSize && isReplaceable; ++i) {
                        for (int j = zLow - xzSize; j <= zLow + xzSize && isReplaceable; ++j) {
                            if (h >= 0 && h < 256) {
                                if (chunk.getBlock(i, h, j)!=null && chunk.getBlock(i, h, j).getBlockID() != AIR) {
                                    isReplaceable = false;
                                }
                            } else
                                isReplaceable = false;
                        }
                    }
                }
            }
            if (!isReplaceable) {
                return;
            }

            for (int h = y + height - 5; h <= y + height; ++h) {
                int restHeight = h - (y + height);
                int xzSize = 5 - restHeight / 2;

                for (int x = xLow - xzSize; x <= xLow + xzSize; ++x) {

                    for (int z = zLow - xzSize; z <= zLow + xzSize; ++z) {

                        if (true||Math.abs(x-xLow) != xzSize || Math.abs(z-zLow) != xzSize // not on bound
                                || rand.nextInt(2) != 0 && restHeight != 0) {
                            chunk.setBlock(OAK_LEAVES, x, h, z);
                        }
                    }
                }
            }

            // genWood
            for (int h = y; h < y+height; ++h) {
                chunk.setBlock(OAK_WOOD, xLow, h, zLow);
            }
        }

    }
}
