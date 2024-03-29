package io.github.medioqrity.engine.world.ChunkUtils;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import org.joml.Vector3f;

import io.github.medioqrity.engine.world.Block;

public class ChunkManager {
    private Map<Pair<Integer, Integer>, Chunk> chunkMap;
    private int[] dx = {1, 0, -1, 0};
    private int[] dz = {0, -1, 0, 1};
//    private ChunkGenerator chunkGenerator;
    private int viewDistanceNear = 12;//12
    private int viewDistanceFar = 16;//16
    private Vector3f generateCenter;
    private Set<Pair<Integer, Integer>> updateList;
    private List<MultiThreadChunkGenerator> generators = new LinkedList<>();
    private List<MultiThreadChunkReader> readers = new LinkedList<>();
    private List<MultiThreadChunkMeshBuilder> builders = new LinkedList<>();
    private List<MultiThreadChunkWriter> writers =new LinkedList<>();
    
    private boolean writeFile=false;
    
    
    
    private final long TIME_THRESHOLD = 1000000L;

    public ChunkManager() {
        chunkMap = new HashMap<>();
//        chunkGenerator = new ChunkGeneratorOverWorld();
        generateCenter = new Vector3f();
        updateList = new HashSet<>();
    }

    public void init() {
//        for (int i = 0; i < WORLD_MAX_WIDTH; ++i) {
//            for (int j = 0; j < WORLD_MAX_LENGTH; ++j) {
//                System.out.println("[INFO] Generating Chunk [" + i + ", " + j + "]");
//                chunkMap.put(new Pair<>(i, j), chunkGenerator.generateChunk(i, j));
//            }
//        }
//        for (Chunk chunk : chunkMap.values()) {
//            System.out.println("[INFO] Generating Chunk Mesh [" + chunk.getx() + ", " + chunk.getz() + "]");
//            chunk.generateMesh(this);
//        }
    }

    public Collection<Chunk> getChunks() {
        return chunkMap.values();
    }

    public void clear() {
        for (Chunk chunk : chunkMap.values()) {
            chunk.clear();
        }
    }

    /**
     * Get block by world coord
     * @param x, y, z world coord of this block
     * @return a block instance
     */
    public Block getBlock(int x, int y, int z) { // x y z are world coord
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk curChunk = chunkMap.get(new Pair<>(chunkX, chunkZ));
        if (curChunk == null) return null;
        return curChunk.getBlock(x & 15, y, z & 15);
    }

//    private boolean valid(int chunkX, int chunkZ) {
//        return 0 <= chunkX && chunkX < WORLD_MAX_WIDTH && 0 <= chunkZ && chunkZ < WORLD_MAX_LENGTH;
//    }

    public void updateBlock(int x, int y, int z, int blockID) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        Chunk curChunk = chunkMap.get(new Pair<>(chunkX, chunkZ));
        if (curChunk == null) return;
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
            if (curChunk == null) continue;
            if (nX != chunkX) curChunk.updateMesh(y >> 4, this);
            if (nZ != chunkZ) curChunk.updateMesh(y >> 4, this);
        }
    }

    private boolean tooFar(Chunk chunk) {
        return Math.abs(((int) generateCenter.x >> 4) - chunk.getx()) > viewDistanceFar ||
               Math.abs(((int) generateCenter.z >> 4) - chunk.getz()) > viewDistanceFar;
    }

    private boolean inSight(int x, int z) {
        return Math.abs(((int) generateCenter.x >> 4) - x) <= viewDistanceNear &&
               Math.abs(((int) generateCenter.z >> 4) - z) <= viewDistanceNear;
    }

    public void update(Vector3f cameraPosition) {
        long time = System.nanoTime();

        generateCenter.set(cameraPosition);

        Iterator<Chunk> chunkTooFarIter = chunkMap.values().iterator();
        while (chunkTooFarIter.hasNext()) {
            Chunk chunk = chunkTooFarIter.next();
            if (tooFar(chunk)) {
                chunkTooFarIter.remove();
                chunk.clear();
            }
        }

        int centerX = (int) generateCenter.x >> 4;
        int centerZ = (int) generateCenter.z >> 4;
        for (int i = centerX - viewDistanceNear; i <= centerX + viewDistanceNear; ++i) {
            for (int j = centerZ - viewDistanceNear; j <= centerZ + viewDistanceNear; ++j) {
                if (i < 0 || j < 0) continue;
                if (!chunkMap.containsKey(new Pair<>(i, j))) {
                    MultiThreadChunkReader reader = new MultiThreadChunkReader(i, j);
                    try {
                        reader.setFile();
                        readers.add(reader);
                        reader.start();
                        updateList.add(new Pair<>(i,j));
                    } catch (FileNotFoundException e) {
                        MultiThreadChunkGenerator generator = new MultiThreadChunkGenerator(i, j);
                        generators.add(generator);
                        generator.start();
                        updateList.add(new Pair<>(i, j));
                    }
                    
                    for (int d = 0; d < 4; ++d) {
                        int nx = i + dx[d];
                        int ny = j + dz[d];
                        if (chunkMap.containsKey(new Pair<>(nx, ny))) {
                            updateList.add(new Pair<>(nx, ny));
                        }
                    }
                }
            }
        }

        try {
            Iterator<MultiThreadChunkReader> iter = readers.iterator();
            while (iter.hasNext()) {
                MultiThreadChunkReader reader = iter.next();
                reader.join();
                chunkMap.put(new Pair<>(reader.getX(), reader.getZ()), reader.getChunk());
                iter.remove();
            }
        } catch (InterruptedException e) {
            System.err.println("[ERROR] ChunkManager.update(): Error in reading chunks.");
            e.printStackTrace();
        }
        
        try {
            Iterator<MultiThreadChunkGenerator> iter = generators.iterator();
            while (iter.hasNext()) {
                MultiThreadChunkGenerator generator = iter.next();
                generator.join();
                chunkMap.put(new Pair<>(generator.getX(), generator.getZ()), generator.getChunk());
                if (writeFile) {
                    MultiThreadChunkWriter writer =new MultiThreadChunkWriter(generator.getChunk());
                    writers.add(writer);
                    writer.start();
                }
                iter.remove();
            }
        } catch (InterruptedException e) {
            System.err.println("[ERROR] ChunkManager.update(): Error in generating chunks.");
            e.printStackTrace();
        }
        
//        System.out.println("gen chunk: " + (System.nanoTime() - test));

        for (Pair<Integer, Integer> p : updateList) {
            MultiThreadChunkMeshBuilder builder = new MultiThreadChunkMeshBuilder(chunkMap.get(p), this);
            builders.add(builder);
            builder.start();
        }
        updateList.clear();

        try {
            Iterator<MultiThreadChunkMeshBuilder> iter = builders.iterator();
            while (iter.hasNext()) {
                if (System.nanoTime() - time > TIME_THRESHOLD) return;
                MultiThreadChunkMeshBuilder builder = iter.next();
                builder.join();
                builder.buildMesh();
                iter.remove();
            }
        } catch (InterruptedException e) {
            System.err.println("[ERROR] ChunkManager.update(): Error in generating chunk meshes.");
            e.printStackTrace();
        }
        
        //writefile
        if (writeFile) {
            try {
                Iterator<MultiThreadChunkWriter> iter = writers.iterator();
                while (iter.hasNext()) {
                    MultiThreadChunkWriter writer = iter.next();
                    writer.join();
                    iter.remove();
                }
            } catch (InterruptedException e) {
                System.err.println("[ERROR] ChunkManager.update(): Error in writing chunks.");
                e.printStackTrace();
            }
        }
    }

}
