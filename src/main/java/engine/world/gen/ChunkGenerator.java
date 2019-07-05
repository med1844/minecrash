package engine.world.gen;

import engine.world.Chunk;

public interface ChunkGenerator {
    Chunk generateChunk(int x, int z);
    
    //hold on
//    void populate(int x, int z); 
}
