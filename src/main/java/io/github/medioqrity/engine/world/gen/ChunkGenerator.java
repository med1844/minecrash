package io.github.medioqrity.engine.world.gen;

import io.github.medioqrity.engine.world.ChunkUtils.Chunk;

public interface ChunkGenerator {

    Chunk generateChunk(int x, int z);

    //hold on
//    void populate(int x, int z); 
}
