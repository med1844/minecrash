package thread;

import engine.world.Chunk;
import engine.world.ChunkManager;
import engine.world.gen.ChunkGenerator;

import java.io.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cache.ByteCache;

public class FileThread implements Runnable {
    public Chunk chunk;
    public ChunkGenerator chunkGenerator;
    File file;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public int id;
    public byte[] buff = new byte[16*16*256];

    public FileThread(Chunk chunk, ChunkGenerator chunkGenerator, File file, int id) {
        this.chunk = chunk;
        this.file = file;
        this.chunkGenerator = chunkGenerator;
        this.id = id;
    }

    @Override
    public void run() {
//        System.out.println("Thread "+id+" running");
        lock.readLock().lock();
        try {
            File file = this.file;
            if (!file.exists() || file.isDirectory())
                throw new FileNotFoundException();
            InputStream in = new FileInputStream(file);
            BufferedInputStream bs = new BufferedInputStream(in, 16 * 16 * 256);
            int len = -1;
            int index = 0;
            while ((len = bs.read(buff)) != -1) {
                for (int i = 0; i < len; i++) {
                    // i j k
                    // 16 256 16
                    chunk.setBlock(buff[i], index >> 12, (index >> 4) & 255, index & 15);
                    index++;
                }
            }

            bs.close();
        } catch (FileNotFoundException e) {
//            chunkGenerator.generateChunk(chunk.getx(), chunk.getz());
            chunkGenerator.generateChunk(chunk);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        synchronized (ChunkManager.countReadTask) {
            ChunkManager.countReadTask--;
        }

//        System.out.println("Thread "+id+" finish "+ChunkManager.countReadTask);
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setChunkGenerator(ChunkGenerator chunkGeneratorOverWorld) {
        this.chunkGenerator = chunkGeneratorOverWorld;
    }

    public Chunk getChunk() { return this.chunk; }

}
