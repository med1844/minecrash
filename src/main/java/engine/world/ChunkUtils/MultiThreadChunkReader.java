package engine.world.ChunkUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MultiThreadChunkReader implements Runnable {
    private int x;
    private int z;
    private Chunk chunk;
    private Thread thread;
    private File file;
    InputStream in;

    public MultiThreadChunkReader(int x, int z) {
        this.x = x;
        this.z = z;
        thread = new Thread(this);
    }

    @Override
    public void run() {
        BufferedInputStream bs = new BufferedInputStream(in, 16 * 16 * 256);
        chunk = new Chunk(x, z);
        
        byte[] buff = new byte[16 * 16 * 256];
        int len = -1;
        int index = 0;
        try {
            while ((len = bs.read(buff)) != -1) {
                for (int i = 0; i < len; i++) {
                    // i j k
                    // 16 256 16
                    chunk.setBlock(buff[i], index >> 12, (index >> 4) & 255, index & 15);
                    index++;
                }
            }
            bs.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        thread.start();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public void setFile() throws FileNotFoundException {
//        this.file = new File("C:\\map\\chunk" + x + "_" + z + ".bin");
//        this.in = new FileInputStream(file);
        throw new FileNotFoundException();
    }
}
