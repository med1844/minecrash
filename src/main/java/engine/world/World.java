package engine.world;

import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.Renderer;

public class World {
    private final int WORLD_MAX_WIDTH = 4;
    private final int WORLD_MAX_LENGHT = 4;
    private Chunk[] chunks;

    public World() {
        chunks = new Chunk[WORLD_MAX_WIDTH * WORLD_MAX_LENGHT];
    }

    public void init() {
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = new Chunk(i / WORLD_MAX_WIDTH, i % WORLD_MAX_WIDTH);
            new ChunkProvider().provideChunk(chunks[i]);
            chunks[i].genBlockList();
        }
    }

    public void render(Renderer renderer, Window window, DirectionalLight directionalLight, Timer timer) {
        for (Chunk chunk : chunks) {
            renderer.render(window, chunk, directionalLight, timer);
        }
    }

    public void clear() {
        for (Chunk chunk : chunks) {
            chunk.clear();
        }
    }

}
