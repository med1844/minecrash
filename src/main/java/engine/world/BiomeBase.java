package engine.world;

import java.util.Random;
import engine.world.Chunk;
import engine.world.gen.ChunkGeneratorOverWorld;

import static engine.world.TextureManager.*;

public class BiomeBase {
    Block topBlock;
    Block fillerBlock;

    public BiomeBase() {
       topBlock=new Block(GRASS, 0, 0, 0);
       fillerBlock=new Block(STONE, 0, 0, 0);
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
            if (y <= rand.nextInt(5)) {
                // y <= 5 COBBLESTONE
                chunk.setBlock(COBBLESTONE, zLow, y, xLow);
            } else {
                Block curBlock = chunk.getBlock(zLow, y, xLow);

                if (curBlock.getType() == AIR) {
                    res = -1;
                } else if (curBlock.getType() == STONE) {
                    if (res == -1) {
                        // y is between air and stone
                        if (cnt <= 0) {
                            topBlock = null;
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
                        } else if (y < seaLevel - 7 - cnt) {
                            // ocean bottom
                            topBlock = null;
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
                        //filling
                        --res;
                        //fill with fillerBlock
                        chunk.setBlock(fillerBlock.getBlockID(),zLow, y, xLow);

                        // if fill sand before, then fill with sandstone
                        if (res == 0 && fillerBlock.getBlockID() == SAND) {
                            res = rand.nextInt(4) + Math.max(0, y - 63);
                            fillerBlock.set(SANDSTONE);
                        }
                    } //res=0 finish
                }
            }
        }
    }
}
