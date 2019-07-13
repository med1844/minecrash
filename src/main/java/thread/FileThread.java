package thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import engine.world.Chunk;
import engine.world.gen.ChunkGenerator;

public class FileThread extends Thread {
    public Chunk chunk;
    public ChunkGenerator chunkGenerator;
    File file;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void run() {
        lock.readLock().lock();
        try {
            File file = this.file;
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
            chunk = chunkGenerator.generateChunk(chunk.getx(), chunk.getz());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
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

}
