package engine.world;

import engine.world.gen.ChunkGeneratorOverWorld;

public class ChunkManager {
    private final int WORLD_MAX_WIDTH = 5;
    private final int WORLD_MAX_LENGTH = 5;
    private Chunk[][] chunks;

    public ChunkManager() {
        chunks = new Chunk[WORLD_MAX_WIDTH][WORLD_MAX_LENGTH];
    }

    public void init() {
        ChunkGeneratorOverWorld chunkGenerator = new ChunkGeneratorOverWorld();
        for (int i = 0; i < chunks.length; ++i) {
            for (int j = 0; j < chunks[i].length; ++j) {
                System.out.println("[INFO] Generating Chunk [" + i + ", " + j + "]");
                chunks[i][j] = chunkGenerator.generateChunk(i, j);
            }
        }
        for (Chunk[] chunkList : chunks) {
            for (Chunk chunk : chunkList) {
                System.out.println("[INFO] Generating Chunk Mesh [" + chunk.getx() + ", " + chunk.getz() + "]");
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
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (0 <= chunkX && chunkX < WORLD_MAX_WIDTH && 0 <= chunkZ && chunkZ < WORLD_MAX_LENGTH) {
            return chunks[chunkX][chunkZ].getBlock(x & 15, y, z & 15);
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
