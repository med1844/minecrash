package engine.world;

import engine.world.gen.ChunkGeneratorOverWorld;

public class ChunkManager {
    private final int WORLD_MAX_WIDTH = 20;
    private final int WORLD_MAX_LENGTH = 20;
    private Chunk[][] chunks;

    public ChunkManager() {
        chunks = new Chunk[WORLD_MAX_WIDTH][WORLD_MAX_LENGTH];
    }

    public void init() {
        ChunkGeneratorOverWorld chunkGenerator = new ChunkGeneratorOverWorld();
        for (int i = 0; i < chunks.length; ++i) {
            for (int j = 0; j < chunks[i].length; ++j) {
                chunks[i][j] = chunkGenerator.generateChunk(i, j);
            }
        }
        for (Chunk[] chunkList : chunks) {
            for (Chunk chunk : chunkList) {
                chunk.generateMesh(this);
            }
        }
    }

    public Chunk[][] getChunks() {
        return chunks;
    }

    public void clear() {
        for (Chunk[] chunk : chunks) {
            for (Chunk value : chunk) {
                value.clear();
            }
        }
    }

    public Block getBlock(int x, int y, int z) {//x y z are world coord
        int chunkX = x / Chunk.getX();
        int chunkZ = z / Chunk.getZ();

        if (0 <= chunkX && chunkX < WORLD_MAX_WIDTH && 0 <= chunkZ && chunkZ < WORLD_MAX_LENGTH) {
            return chunks[chunkX][chunkZ].getBlock(x % Chunk.getX(), y, z % Chunk.getZ());
        } else {
            return null;
        }
    }

}

/*
 * 01234
 * 56789
 *
 * */
