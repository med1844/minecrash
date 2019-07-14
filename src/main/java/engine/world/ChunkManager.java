package engine.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.joml.Vector3f;

import com.sun.org.apache.bcel.internal.generic.NEW;

import engine.maths.Pair;
import engine.world.gen.ChunkGenerator;
import engine.world.gen.ChunkGeneratorOverWorld;
import thread.FileThread;

public class ChunkManager {
    private final int WORLD_MAX_WIDTH = 50;
    private final int WORLD_MAX_LENGTH = 50;
    private int generateDistance = 8;
    private Pair generateCenter;

    private HashMap<Pair, Chunk> chunkMap;
    private int[] dx = { 1, 0, -1, 0 };
    private int[] dz = { 0, -1, 0, 1 };
    private ChunkGenerator chunkGenerator;
    private File[][] files;

    public ChunkManager() {
        chunkMap = new HashMap<Pair, Chunk>();
        chunkGenerator = new ChunkGeneratorOverWorld();
    }

    public void init() {
        generateCenter = new Pair(0, 0);
        files = new File[50][50];
        for (int i = 0; i < 50; ++i) {
            for (int j = 0; j < 50; ++j) {
                String filename = "C:\\map\\chunk" + i + "_" + j + ".txt";
                files[i][j] = new File(filename);
            }
        }

//        for (int i = 0; i < 50; ++i) {
//            for (int j = 0; j < 50; ++j) {
//                System.out.println("[INFO] writing Chunk [" + i + ", " + j + "]");
//                writeChunkToFile(chunkGenerator.generateChunk(i, j));
////                chunkMap.put(new Pair<>(i, j), chunkGenerator.generateChunk(i, j));
//            }
//        }

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                long beginTime = System.currentTimeMillis();
                chunkMap.put(new Pair(i, j), readChunkFromFile(i, j));
                System.out.println("[INFO] reading Chunk [" + i + ", " + j + "]" + " ,use time: "
                        + (System.currentTimeMillis() - beginTime));
            }
        }

        for (Chunk chunk : chunkMap.values()) {
//            System.out.println("[INFO] Generating Chunk Mesh [" + chunk.getx() + ", " + chunk.getz() + "]");
            chunk.generateMesh(this);
        }

    }

    public Collection<Chunk> getChunks(Vector3f position) {
        return chunkMap.values();
    }

    public void clear() {
        for (Chunk chunk : chunkMap.values()) {
            chunk.clear();
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
//        System.out.println("centerx:"+generateCenter.getKey()+" centerz:"+generateCenter.getValue()+" centery:"+(int)position.y);

        HashSet<Pair> posSet = new HashSet<Pair>();
        for (Chunk chunk : chunkMap.values()) {
            if (!valid(chunk.getx(), chunk.getz()) || outOfSight(chunk.getx(), chunk.getz())) {
//                System.out.println("[INFO] removing Chunk [" + chunk.getx() + ", " + chunk.getz() + "]");
                posSet.add(new Pair(chunk.getx(), chunk.getz()));
            }
        }
        for (Pair p : posSet) {
            chunkMap.remove(p);
        }
        int cnt = 0;
        posSet.clear();
        for (int i = -generateDistance + generateCenter.first; i <= generateDistance + generateCenter.first; ++i) {
            int d = (int) Math.sqrt(
                    generateDistance * generateDistance - (generateCenter.first - i) * (generateCenter.first - i));
            for (int j = -d + generateCenter.second; j <= d + generateCenter.second; ++j) {
                if (!valid(i, j)) {
                    continue;
                }
                if (chunkMap.get(new Pair(i, j)) == null) {
//                    System.out.println("[INFO] Generating Chunk [" + i + ", " + j + "]");
//                    chunkMap.put(new Pair<>(i, j), chunkGenerator.generateChunk(i, j));
//                    Chunk newChunk = readChunkFromFile(i, j);
                    Chunk newChunk = new Chunk(i, j);
                    long tmp = System.currentTimeMillis();
                    readChunkFromFile(newChunk);
//                    System.out.println("[INFO] read Chunk [" + i + ", " + j + "]" + " use time: "
//                            + (System.currentTimeMillis() - tmp));
//                    if (newChunk == null) {
//                        System.out.println("[INFO] create Chunk[" + i + ", " + j + "]" + " failed");
//                    }
                    chunkMap.put(new Pair(i, j), newChunk);
                    posSet.add(new Pair(i, j));
                    cnt++;
                }
            }
        }
        
        
        for (Pair p : posSet) {
            chunkMap.get(p).generateMesh(this);
        }

        System.out.println("ChunkManager.update() finish, use time: " + (System.currentTimeMillis() - beginTime)
                + " update " + cnt + " chunks");
    }

    public Block getBlock(int x, int y, int z) { // x y z are world coord
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (!valid(chunkX, chunkZ))
            return null;

        Chunk curChunk = chunkMap.get(new Pair(chunkX, chunkZ));
        if (curChunk == null)
            return null;
        return curChunk.getBlock(x & 15, y, z & 15);
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

        Chunk curChunk = chunkMap.get(new Pair(chunkX, chunkZ));
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
            curChunk = chunkMap.get(new Pair(nX, nZ));
            if (curChunk == null)
                continue;

            if (nX != chunkX)
                curChunk.updateMesh(y >> 4, this);
            if (nZ != chunkZ)
                curChunk.updateMesh(y >> 4, this);
        }
    }

    public Chunk readChunkFromFile(int x, int z) {
        Chunk chunk = new Chunk(x, z);
//        String filename = "C:\\map\\chunk" + x + "_" + z + ".txt";
        try {
            File file = files[x][z];
//            File file = new File(filename);
            if (!file.exists() || file.isDirectory())
                throw new FileNotFoundException();
            BufferedReader br = new BufferedReader(new FileReader(file), 16 * 16 * 256);

            char[] buff = new char[16 * 16 * 256];
            int len = -1;
            int index = 0;
            while ((len = br.read(buff)) != -1) {
                for (int i = 0; i < len; i++) {
                    // i j k
                    // 16 256 16
                    chunk.setBlock(buff[i] & 255, index >> 12, (index >> 4) & 255, index & 15);
                    index++;
                    chunk.setBlock(buff[i] >> 8, index >> 12, (index >> 4) & 255, index & 15);
                    index++;
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            chunk = chunkGenerator.generateChunk(x, z);
            return chunk;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return chunk;
    }

    public void readChunkFromFile(Chunk chunk) {
        FileThread fileThread = new FileThread();
        fileThread.setChunk(chunk);
        fileThread.setChunkGenerator(chunkGenerator);
        fileThread.setFile(files[chunk.getx()][chunk.getz()]);
        fileThread.start();
        try {
            fileThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void writeChunkToFile(Chunk chunk) {
//        String filename = "C:\\map\\chunk" + chunk.getx() + "_" + chunk.getz() + ".txt";
        try {
            File file = files[chunk.getx()][chunk.getz()];
//            File file = new File(filename);
            if (!file.exists())
                file.createNewFile();

            OutputStream os = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(writer);
            char data = 0;
            int t = 0;
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 256; j++) {
                    for (int k = 0; k < 16; k++) {
                        if (t == 0) {
                            data = (char) chunk.getBlock(i, j, k).getBlockID();
                            t++;
                        } else {
                            data += ((char) chunk.getBlock(i, j, k).getBlockID()) << 8;
                            t = 0;
                            bw.append(data);
                        }
                    }
                }
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}