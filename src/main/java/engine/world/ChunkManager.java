package engine.world;

import engine.world.gen.ChunkGeneratorOverWorld;

public class ChunkManager {
    private final int WORLD_MAX_WIDTH = 2;
    private final int WORLD_MAX_LENGTH = 2;
    private Chunk[][] chunks;
    private int[] dx = {1, 0, -1, 0};
    private int[] dz = {0, -1, 0, 1};

    public ChunkManager() {
        chunks = new Chunk[WORLD_MAX_WIDTH][WORLD_MAX_LENGTH];
    }

    public void init() {
        ChunkGeneratorOverWorld chunkGenerator = new ChunkGeneratorOverWorld(998442353L * System.nanoTime());

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

    private boolean valid(int chunkX, int chunkZ) {
        return 0 <= chunkX && chunkX < WORLD_MAX_WIDTH && 0 <= chunkZ && chunkZ < WORLD_MAX_LENGTH;
    }

    public void updateBlock(int x, int y, int z, int blockID) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (valid(chunkX, chunkZ)) {
            chunks[chunkX][chunkZ].setBlock(blockID, x & 15, y, z & 15);
            chunks[chunkX][chunkZ].updateMesh(y >> 4, this);
            if ((y & 15) == 0) chunks[chunkX][chunkZ].updateMesh((y >> 4) - 1, this);
            if ((y & 15) == 15) chunks[chunkX][chunkZ].updateMesh((y >> 4) + 1, this);
            for (int d = 0; d < 4; ++d) {
                int nx = x + dx[d], nz = z + dz[d];
                int nX = nx >> 4, nZ = nz >> 4;
                if (valid(nX, nZ)) {
                    if (nX != chunkX) chunks[nX][nZ].updateMesh(y >> 4, this);
                    if (nZ != chunkZ) chunks[nX][nZ].updateMesh(y >> 4, this);
                }
            }
        }
    }

}

/*
 * 01234
 * 56789
 *
 * */
