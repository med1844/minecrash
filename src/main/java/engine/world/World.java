package engine.world;

import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.Renderer;
import engine.world.gen.ChunkGeneratorOverWorld;

public class World {
    private final int WORLD_MAX_WIDTH = 4;
    private final int WORLD_MAX_LENGTH = 4;
    private Chunk[][] chunks;

    public World() {
        chunks = new Chunk[WORLD_MAX_WIDTH][WORLD_MAX_LENGTH];
    }

    public void init() {
        ChunkGeneratorOverWorld chunkGenerator=new ChunkGeneratorOverWorld();
        for (int i = 0; i < chunks.length; i++) {
            for (int j=0;j<chunks[i].length;j++) {
//                chunks[i][j] = new Chunk(i, j);
                chunks[i][j] = chunkGenerator.generateChunk(i, j);
                chunks[i][j].genBlockList();
            }
        }
    }

    public void render(Renderer renderer, Window window, DirectionalLight directionalLight, Timer timer) {
        for (int i = 0; i < chunks.length; i++) {
            for (int j=0;j<chunks[i].length;j++) {
                renderer.render(window, chunks[i][j], directionalLight, timer);
            }
        }
    }

    public void clear() {
        for (int i = 0; i < chunks.length; i++) {
            for (int j=0;j<chunks[i].length;j++) {
                chunks[i][j].clear();
            }
        }
    }
    
    public Block getBlock(int x,int y,int z) {//x y z are world coord
        int chunkx=x/Chunk.getX();
        int chunkz=z/Chunk.getZ();
        
        return chunks[chunkx][chunkz].getBlock(x%Chunk.getX(),y,z%Chunk.getZ());
    }

}

/*
 * 01234
 * 56789
 * 
 * */
