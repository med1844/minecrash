package engine.world.ChunkUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MultiThreadChunkWriter implements Runnable {
    private int x;
    private int z;
    private Chunk chunk;
    private Thread thread;
    private File file;
    OutputStream out;

    public MultiThreadChunkWriter(Chunk chunk) {
        this.x = chunk.getx();
        this.z = chunk.getz();
        this.chunk=chunk;
        thread = new Thread(this);
    }

    @Override
    public void run() {
        file = new File("E:\\map\\chunk" + x + "_" + z + ".bin");
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedOutputStream bs = new BufferedOutputStream(out, 16 * 16 * 256);
        try {
            byte[] buff=new byte[16*256*16];
            int index=0;
            for (int i=0;i<16;i++) {
                for (int j=0;j<256;j++) {
                    for (int k=0;k<16;k++) {
                        buff[index++]=(byte)chunk.getBlock(i, j, k).getBlockID();
                    }
                }
            }
            bs.write(buff);
            bs.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void start() {
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }

}
