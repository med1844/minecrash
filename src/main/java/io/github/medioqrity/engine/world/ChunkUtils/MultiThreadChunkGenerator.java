package io.github.medioqrity.engine.world.ChunkUtils;

import io.github.medioqrity.engine.world.gen.ChunkGenerator;
import io.github.medioqrity.engine.world.gen.ChunkGeneratorOverWorld;

public class MultiThreadChunkGenerator implements Runnable {

    private int x;
    private int z;
    private ChunkGenerator chunkGenerator;
    private Chunk chunk;
    private Thread thread;

    public MultiThreadChunkGenerator(int x, int z) {
        this.x = x;
        this.z = z;
        this.chunkGenerator = new ChunkGeneratorOverWorld();
        thread = new Thread(this);
    }

    @Override
    public void run() {
        chunk = chunkGenerator.generateChunk(x, z);
    }

    public void start() {
        thread.start();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

}
