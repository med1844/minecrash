package engine.world;

import static engine.world.TextureManager.*;

import engine.maths.Perlin;

public class ChunkProvider {
    
    
    public void provideChunk(Chunk chunk) {
        setBlocksInChunk(chunk);
    }
    
    public void setBlocksInChunk(Chunk chunk) {
        try {
            for (int x = 0; x < Chunk.getX(); ++x) for (int y = 0; y < Chunk.getY(); ++y) for (int z = 0; z < Chunk.getZ(); ++z) 
                chunk.setBlocks(x,y,z,new Block(AIR, (chunk.getx() << 4) + x, y, (chunk.getz() << 4) + z));
//            for (int x = 0; x < Chunk.getX(); ++x) {
//                for (int z = 0; z < Chunk.getZ(); ++z) {
//                    int h=(int)(((new Perlin().PerlinNoise( (chunk.getx() << 4) + x, (chunk.getz() << 4) + z))+1)/2.0*5);
//                    for (int y = 0; y < h; ++y) {
//                        chunk.setBlocks(x,y,z,new Block(STONE, (chunk.getx() << 4) + x, y, (chunk.getz() << 4) + z));
//                    }
//                    chunk.setBlocks(x,h,z,new Block(GRASS, (chunk.getx() << 4) + x, h, (chunk.getz() << 4) + z));
//                }
//            }
             for (int x=0;x<Chunk.getX();x++) {
                 for (int z=0;z<Chunk.getZ();z++) {
                     int h=(int)(Math.abs(Math.sin( ((chunk.getx() << 4) + x)/5.0f)*16) );
                     for (int y=0;y<h;y++) {
                         chunk.setBlocks(x,y,z,new Block(STONE, (chunk.getx() << 4) + x, y, (chunk.getz() << 4) + z));
                     }
                     chunk.setBlocks(x,h,z,new Block(GRASS, (chunk.getx() << 4) + x, h, (chunk.getz() << 4) + z));
                 }    
             }
        } catch (Exception e) {
            System.err.println("[ERROR] Chunk.init():\r\n" + e);
            System.exit(-1);
        }        
    }
    
    
    
}
