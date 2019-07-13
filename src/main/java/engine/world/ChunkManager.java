package engine.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import engine.world.gen.ChunkGenerator;
import engine.world.gen.ChunkGeneratorOverWorld;
import javafx.util.Pair;

public class ChunkManager {
    private final int WORLD_MAX_WIDTH = 2;
    private final int WORLD_MAX_LENGTH = 2;
    private Map<Pair<Integer, Integer>, Chunk> chunkMap;
    private int[] dx = {1, 0, -1, 0};
    private int[] dz = {0, -1, 0, 1};
    private ChunkGenerator chunkGenerator;

    public ChunkManager() {
        chunkMap = new HashMap<>();
        chunkGenerator = new ChunkGeneratorOverWorld();
    }

    public void init() {
        for (int i = 0; i < WORLD_MAX_WIDTH; ++i) {
            for (int j = 0; j < WORLD_MAX_LENGTH; ++j) {
                System.out.println("[INFO] Generating Chunk [" + i + ", " + j + "]");
                chunkMap.put(new Pair<>(i, j), chunkGenerator.generateChunk(i, j));
            }
        }
        for (Chunk chunk : chunkMap.values()) {
            System.out.println("[INFO] Generating Chunk Mesh [" + chunk.getx() + ", " + chunk.getz() + "]");
            chunk.generateMesh(this);
        }
    }

    public Collection<Chunk> getChunks() {
        return chunkMap.values();
    }

    public void clear() {
        for (Chunk chunk : chunkMap.values()) {
            chunk.clear();
        }
    }

    public Block getBlock(int x, int y, int z) { // x y z are world coord
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk curChunk = chunkMap.get(new Pair<>(chunkX, chunkZ));
        if (curChunk == null) return null;
        return curChunk.getBlock(x & 15, y, z & 15);
    }

    private boolean valid(int chunkX, int chunkZ) {
        return 0 <= chunkX && chunkX < WORLD_MAX_WIDTH && 0 <= chunkZ && chunkZ < WORLD_MAX_LENGTH;
    }

    public void updateBlock(int x, int y, int z, int blockID) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk curChunk = chunkMap.get(new Pair<>(chunkX, chunkZ));
        if (curChunk == null) return;
        curChunk.setBlock(blockID, x & 15, y, z & 15);
        curChunk.updateMesh(y >> 4, this);
        if ((y & 15) == 0)
            curChunk.updateMesh((y >> 4) - 1, this);
        if ((y & 15) == 15)
            curChunk.updateMesh((y >> 4) + 1, this);
        for (int d = 0; d < 4; ++d) {
            int nx = x + dx[d], nz = z + dz[d];
            int nX = nx >> 4, nZ = nz >> 4;
            curChunk = chunkMap.get(new Pair<>(nX, nZ));
            if (curChunk == null) continue;
            if (nX != chunkX) curChunk.updateMesh(y >> 4, this);
            if (nZ != chunkZ) curChunk.updateMesh(y >> 4, this);
        }
    }

}
