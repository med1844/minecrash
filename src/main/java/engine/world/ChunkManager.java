package engine.world;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

import org.joml.Vector3f;

import engine.maths.Pair;
import engine.world.gen.ChunkGenerator;
import engine.world.gen.ChunkGeneratorOverWorld;
import thread.FileThread;

public class ChunkManager {
    private final int WORLD_MAX_WIDTH = 50;
    private final int WORLD_MAX_LENGTH = 50;
    private int generateDistance = 4;
    private Pair generateCenter;
    private int id = 0;

    private int[] dx = { 1, 0, -1, 0 };
    private int[] dz = { 0, -1, 0, 1 };
    private ChunkGenerator chunkGenerator;
    private File[][] files;
    private Chunk[][] chunks = new Chunk[WORLD_MAX_WIDTH][WORLD_MAX_LENGTH];
    private boolean[][] hasChunk = new boolean[WORLD_MAX_WIDTH][WORLD_MAX_LENGTH];
    private HashSet<Pair> posSet;

    public static Integer countReadTask = 0;

    public ChunkManager() {
        posSet = new HashSet<Pair>();
        chunkGenerator = new ChunkGeneratorOverWorld();
        generateCenter = new Pair(0, 0);
        files = new File[WORLD_MAX_WIDTH][WORLD_MAX_LENGTH];
        for (int i = 0; i < WORLD_MAX_WIDTH; ++i) {
            for (int j = 0; j < WORLD_MAX_LENGTH; ++j) {
                String filename = "C:\\map\\chunk" + i + "_" + j + ".bin";
                files[i][j] = new File(filename);
            }
        }
    }

    public void init() {

//        for (int i = 0; i < 50; ++i) {
//            for (int j = 0; j < 50; ++j) {
//                System.out.println("[INFO] writing Chunk [" + i + ", " + j + "]");
//                writeChunkToFile(chunkGenerator.generateChunk(i, j));
//            }
//        }
        long preTime=System.currentTimeMillis();
        countReadTask = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                hasChunk[i][j] = true;
                posSet.add(new Pair(i, j));
                long beginTime = System.currentTimeMillis();
                countReadTask++;
                chunks[i][j] = readChunkFromFile(i, j);
//                chunks[i][j] = chunkGenerator.generateChunk(i, j);
                System.out.println("[INFO] reading Chunk [" + i + ", " + j + "]" + " ,use time: "
                        + (System.currentTimeMillis() - beginTime));
            }
        }
        
        try {
            while (countReadTask > 0) {
                Thread.sleep(1);
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (Pair p : posSet) {
            chunks[p.first][p.second].generateMesh(this);
        }
        System.out.println("ChunkManager.init() done "+(System.currentTimeMillis()-preTime));
    }

    public void clear() {
        for (Pair p : posSet) {
            chunks[p.first][p.second].clear();
        }
    }

    public void update(Vector3f position) {
        if (generateCenter == null)
            generateCenter = new Pair((int) position.x / 16, (int) position.z / 16);
        else if (generateCenter.first == (int) position.x / 16 && generateCenter.second == (int) position.z / 16) {
            return;
        }
        long beginTime = System.currentTimeMillis();

        generateCenter = new Pair((int) position.x / 16, (int) position.z / 16);

        HashSet<Pair> removeSet = new HashSet<Pair>();
        for (Pair p : posSet) {
            if (!valid(p.first, p.second) || outOfSight(p.first, p.second)) {
                removeSet.add(p);
            }
        }

        for (Pair p : removeSet) {
            chunks[p.first][p.second] = null;
            hasChunk[p.first][p.second] = false;
            posSet.remove(p);
        }

        HashSet<Pair> addSet = new HashSet<Pair>();

        int cnt = 0;
        id = 0;
        countReadTask = 0;
        for (int i = -generateDistance + generateCenter.first; i <= generateDistance + generateCenter.first; ++i) {
            int d = (int) Math.sqrt(
                    generateDistance * generateDistance - (generateCenter.first - i) * (generateCenter.first - i));
            for (int j = -d + generateCenter.second; j <= d + generateCenter.second; ++j) {
                if (!valid(i, j)) {
                    continue;
                }
                if (!hasChunk[i][j]) {
                    countReadTask++;
                    cnt++;
//                    chunks[i][j] = chunkGenerator.generateChunk(i, j);
                    chunks[i][j] = readChunkFromFile(i, j);
                    addSet.add(new Pair(i, j));
                    posSet.add(new Pair(i, j));
                    hasChunk[i][j] = true;
                }
            }
        }
        
        try {
            while (countReadTask > 0) {
                Thread.sleep(1);
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Pair p : addSet) {
            chunks[p.first][p.second].generateMesh(this);
        }
        
        
        System.out.println("ChunkManager.update() finish, use time: " + (System.currentTimeMillis() - beginTime)
                + " update " + cnt + " chunks "+" maxMemory:"+Runtime.getRuntime().maxMemory()/1024/1024+" freeMemory:"+Runtime.getRuntime().freeMemory()/1024/1024 );
    }

    public Block getBlock(int x, int y, int z) { // x y z are world coord
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (!valid(chunkX, chunkZ))
            return null;

        if (chunks[chunkX][chunkZ] == null)
            return null;
        return chunks[chunkX][chunkZ].getBlock(x & 15, y, z & 15);
    }

    private boolean valid(int chunkX, int chunkZ) {
        return 0 <= chunkX && chunkX < WORLD_MAX_WIDTH && 0 <= chunkZ && chunkZ < WORLD_MAX_LENGTH;
    }

    private boolean outOfSight(int chunkX, int chunkZ) {
        return (chunkX - generateCenter.first) * (chunkX - generateCenter.first) + (chunkZ - generateCenter.second)
                * (chunkZ - generateCenter.second) > generateDistance * generateDistance;
    }

    public void updateBlock(int x, int y, int z, int blockID) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (!valid(chunkX, chunkZ))
            return;

        if (chunks[chunkX][chunkZ] == null)
            return;

        chunks[chunkX][chunkZ].setBlock(blockID, x & 15, y, z & 15);
        chunks[chunkX][chunkZ].updateMesh(y >> 4, this);
        if ((y & 15) == 0)
            chunks[chunkX][chunkZ].updateMesh((y >> 4) - 1, this);
        if ((y & 15) == 15)
            chunks[chunkX][chunkZ].updateMesh((y >> 4) + 1, this);
        for (int d = 0; d < 4; ++d) {
            int nx = x + dx[d], nz = z + dz[d];
            int nX = nx >> 4, nZ = nz >> 4;
            if (chunks[nX][nZ] == null)
                continue;

            if (nX != chunkX)
                chunks[nX][nZ].updateMesh(y >> 4, this);
            if (nZ != chunkZ)
                chunks[nX][nZ].updateMesh(y >> 4, this);
        }
    }

    public Chunk readChunkFromFile(int x, int z) {
        Chunk chunk = new Chunk(x, z);
//        FileThread fileThread=new FileThread(chunk, chunkGenerator, files[x][z], id++);
//        fileThread.run();
        Thread fileThread=new Thread(new FileThread(chunk, chunkGenerator, files[x][z], id++));
        fileThread.start();
        return chunk;
    }

    public void writeChunkToFile(Chunk chunk) {
        try {
            File file = files[chunk.getx()][chunk.getz()];
            if (!file.exists())
                file.createNewFile();

            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bs = new BufferedOutputStream(os, 16 * 16 * 256);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 256; j++) {
                    for (int k = 0; k < 16; k++) {
                        bs.write(chunk.getBlock(i, j, k).getBlockID());
                    }
                }
                bs.flush();
            }
            bs.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public Iterable<Chunk> getChunks() {
        return new ChunkSet(posSet.iterator(), chunks);
    }

}