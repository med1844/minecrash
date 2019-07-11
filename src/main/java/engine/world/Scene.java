package engine.world;

import engine.graphics.DirectionalLight;
import engine.world.ChunkManager;
import static engine.world.TextureManager.*;

import org.joml.Vector3f;

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

    public void destroyBlock(Vector3f selectedBlockPos) {
        chunkManager.updateBlock((int) selectedBlockPos.x, (int) selectedBlockPos.y, (int) selectedBlockPos.z, AIR);
    }

    public void putBlock(Vector3f selectedBlockPos, int blockID) {
        chunkManager.updateBlock((int) selectedBlockPos.x, (int) selectedBlockPos.y, (int) selectedBlockPos.z, blockID);
    }

}
