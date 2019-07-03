package engine.world;

public class World {
    private static final int WORLD_MAX_WIDTH=4;
    private static final int WORLD_MAX_LENGHT=4;
    
    private Chunk[] chunks;
    
    public World() {
        chunks=new Chunk[WORLD_MAX_WIDTH*WORLD_MAX_LENGHT];
    }
    
    public void init() {
        
    }
    
    
}
