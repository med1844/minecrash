package engine.world;

import engine.graphics.DirectionalLight;
import engine.world.ChunkManager;

public class Scene {
    public ChunkManager chunkManager;
    public DirectionalLight light;

    public Scene(ChunkManager chunkManager, DirectionalLight light) {
        this.chunkManager = chunkManager;
        this.light = light;
    }

    public void init() {
        chunkManager.init();
    }

    public void clear() {
        chunkManager.clear();
    }
}
