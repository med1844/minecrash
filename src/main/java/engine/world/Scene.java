package engine.world;

import engine.graphics.DirectionalLight;

public class Scene {
    public Chunk[] chunks;
    public DirectionalLight light;

    public Scene(Chunk[] chunks, DirectionalLight light) {
        this.chunks = chunks;
        this.light = light;
    }

    public void init() {
        for (Chunk chunk : chunks) {
            chunk.init();
            chunk.genBlockList();
        }
    }

    public void clear() {
        for (Chunk chunk : chunks) {
            chunk.clear();
        }
    }
}
