package engine;

import engine.graphics.Mesh;

public class Chunk {

    private int x, y; // the chunk coordinates of current chunk
    private int[][][] blockData;
    private Mesh mesh;
    private int[][] biomeData;

    public Chunk() {
        blockData = new int[16][256][16]; // retrieve data through (x, y, z)
        biomeData = new int[16][16];
    }

    public void init() {
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    blockData = MC_GRASS_BLOCK;
                }
            }
        }
    }
}
