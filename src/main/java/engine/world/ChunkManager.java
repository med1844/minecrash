package engine.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.joml.Vector3f;


import engine.world.gen.ChunkGenerator;
import engine.world.gen.ChunkGeneratorOverWorld;
import javafx.util.Pair;
import jdk.nashorn.internal.ir.annotations.Ignore;

public class ChunkManager {
    private final int WORLD_MAX_WIDTH = 500;
    private final int WORLD_MAX_LENGTH = 500;
    private int generateDistance = 8;
    private Pair<Integer, Integer> generateCenter;

    private Map<Pair<Integer, Integer>, Chunk> chunkMap;
    private int[] dx = { 1, 0, -1, 0 };
    private int[] dz = { 0, -1, 0, 1 };
    private ChunkGenerator chunkGenerator;

    public ChunkManager() {
        chunkMap = new HashMap<>();
        chunkGenerator = new ChunkGeneratorOverWorld();
    }

    public void init() {
        generateCenter=new Pair<Integer, Integer>(0, 0);
        
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                System.out.println("[INFO] Generating Chunk [" + i + ", " + j + "]");
                chunkMap.put(new Pair<>(i, j), chunkGenerator.generateChunk(i, j));
            }
        }
        
        for (Chunk chunk : chunkMap.values()) {
//            System.out.println("[INFO] Generating Chunk Mesh [" + chunk.getx() + ", " + chunk.getz() + "]");
            chunk.generateMesh(this);
        }
    }

    public Collection<Chunk> getChunks(Vector3f position) {
//        HashSet<Chunk> chunks=new HashSet<Chunk>();
//        for (Chunk chunk:chunkMap.values()) {
//            if (Math.abs(chunk.getx()-position.x/16)+Math.abs(chunk.getz()-position.z/16)<=) {
//                chunks.add(chunk);
//            }
//        }
        return chunkMap.values();
    }

    public void clear() {
        for (Chunk chunk : chunkMap.values()) {
            chunk.clear();
        }
    }

    public void update(Vector3f position) {
        if (generateCenter==null) generateCenter=new Pair<>((int)position.x/16,(int)position.z/16);
        else if (generateCenter.getKey().intValue()==(int)position.x/16 &&
                 generateCenter.getValue().intValue()==(int)position.z/16) {
            return ;
        }
        generateCenter=new Pair<Integer, Integer>((int)position.x/16, (int)position.z/16);
        System.out.println("centerx:"+generateCenter.getKey()+" centerz:"+generateCenter.getValue()+" centery:"+(int)position.y);
        
        
        HashSet<Pair<Integer,Integer> >posSet=new HashSet<Pair<Integer,Integer>>();
        for (Chunk chunk:chunkMap.values()) {
            if (!valid(chunk.getx(), chunk.getz()) || outOfSight(chunk.getx(), chunk.getz())) {
//                System.out.println("[INFO] removing Chunk [" + chunk.getx() + ", " + chunk.getz() + "]");
                posSet.add(new Pair<>(chunk.getx(), chunk.getz()));
            }
        }
        for (Pair<Integer,Integer>p:posSet) {
            chunkMap.remove(p);
        }
        
        posSet.clear();
        for (int i = -generateDistance+generateCenter.getKey(); i <= generateDistance+generateCenter.getKey(); ++i) {
            int d=(int)Math.sqrt(generateDistance*generateDistance-(generateCenter.getKey()-i)*(generateCenter.getKey()-i));
            for (int j = -d+generateCenter.getValue(); j <=d+generateCenter.getValue() ; ++j) {
                if (!valid(i, j)) {
//                    System.out.println("invalid "+i+" "+j+" centerx:"+generateCenter.getKey()+" centery:"+generateCenter.getValue());
                    continue;
                }
                if (chunkMap.get(new Pair<>(i, j))==null) {
//                    System.out.println("[INFO] Generating Chunk [" + i + ", " + j + "]");
                    chunkMap.put(new Pair<>(i, j), chunkGenerator.generateChunk(i, j));
                    posSet.add(new Pair<>(i,j));
                }
            }
        }
        for (Pair<Integer,Integer>p:posSet) {
            chunkMap.get(p).generateMesh(this);
        }
    }

    public Block getBlock(int x, int y, int z) { // x y z are world coord
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (!valid(chunkX, chunkZ))
            return null;

        Chunk curChunk = chunkMap.get(new Pair<>(chunkX, chunkZ));
        if (curChunk == null)
            return null;
        return curChunk.getBlock(x & 15, y, z & 15);
    }

    private boolean valid(int chunkX, int chunkZ) {
        return 0 <= chunkX && chunkX < WORLD_MAX_WIDTH && 0 <= chunkZ && chunkZ < WORLD_MAX_LENGTH;
    }
    
    private boolean outOfSight(int chunkX,int chunkZ) {
        return (chunkX-generateCenter.getKey())*(chunkX-generateCenter.getKey())+
        (chunkZ-generateCenter.getValue())*(chunkZ-generateCenter.getValue())>generateDistance*generateDistance;
    }

    public void updateBlock(int x, int y, int z, int blockID) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (!valid(chunkX, chunkZ))
            return;

        Chunk curChunk = chunkMap.get(new Pair<>(chunkX, chunkZ));
        if (curChunk == null)
            return;

        curChunk.setBlock(blockID, x & 15, y, z & 15);
        curChunk.updateMesh(y >> 4, this);
        if ((y & 15) == 0)
            curChunk.updateMesh((y >> 4) - 1, this);
        if ((y & 15) == 15)
            curChunk.updateMesh((y >> 4) + 1, this);
        for (int d = 0; d < 4; ++d) {
            int nx = x + dx[d], nz = z + dz[d];
            int nX = nx >> 4, nZ = nz >> 4;
            curChunk = chunkMap.get(new Pair<>(nX, nZ));
            if (curChunk == null)
                continue;

            if (nX != chunkX)
                curChunk.updateMesh(y >> 4, this);
            if (nZ != chunkZ)
                curChunk.updateMesh(y >> 4, this);
        }
    }

}
