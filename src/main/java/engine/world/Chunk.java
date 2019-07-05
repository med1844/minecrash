package engine.world;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static engine.world.TextureManager.*;

public class Chunk {

    private int x, z; // the chunk coordinates of current chunk
    private Block[][][] blocks;
    public List<Block> renderList; // stores the block list that should be rendered
    private static final int X = 16;
    private static final int Y = 16;
    private static final int Z = 16;
    private static final int[] dx = {1, -1, 0,  0, 0,  0};
    private static final int[] dy = {0,  0, 1, -1, 0,  0};
    private static final int[] dz = {0,  0, 0,  0, 1, -1};

    public Chunk() {
        this.x = 0;
        this.z = 0;
    }

    public Chunk(int x, int y) {
        this.x = x;
        this.z = y;
        blocks = new Block[X][Y][Z]; // retrieve data through (x, y, z)
        renderList = new LinkedList<>();
    }

    public void init() {
        Random rand = new Random();
        try {
            for (int x = 0; x < X; ++x) for (int y = 0; y < Y; ++y) for (int z = 0; z < Z; ++z) blocks[x][y][z] = new Block(AIR, (this.x << 4) + x, y, (this.z << 4) + z);
            for (int x = 0; x < X; ++x) {
                for (int z = 0; z < Z; ++z) {
                    for (int y = 0; y < 6 + rand.nextInt(5); ++y) {
                        blocks[x][y][z] = new Block(1 + rand.nextInt(5), (this.x << 4) + x, y, (this.z << 4) + z);
                    }
                }
            }
            for (int y = 0; y < Y; ++y) {
                blocks[5][y][5] = new Block(PLANKS, (this.x << 4) + x, y, (this.z << 4) + z);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Chunk.init():\r\n" + e);
            System.exit(-1);
        }
    }

    private boolean valid(int x, int y, int z) {
        return 0 <= x && x < X &&
               0 <= y && y < Y &&
               0 <= z && z < Z;
    }

    /**
     * this method generates the block list that would be loaded and
     * rendered, in order to improve the performance
     */
    public void genBlockList() {
        boolean flag;
        int x, y, z, nx, ny, nz, d;
        for (x = 0; x < X; ++x) {
            for (y = 0; y < Y; ++y) {
                for (z = 0; z < Z; ++z) {
                    if (blocks[x][y][z].equals(AIR)) continue;
                    flag = false; // flag: whether this block will be rendered or not
                    for (d = 0; d < 6 && !flag; ++d) {
                        nx = x + dx[d];
                        ny = y + dy[d];
                        nz = z + dz[d];
                        if (valid(nx, ny, nz)) {
                            if (blocks[nx][ny][nz].equals(AIR)) {
                                flag = true;
                            }
                        } else {
                            flag = true;
                        }
                    }
                    if (flag) {
                        renderList.add(blocks[x][y][z]);
                    }
                }
            }
        }
    }

    public void generateMesh() {
        
    }

    public void clear() {
        for (Block cur : renderList) {
            cur.clear();
        }
    }

    public int getx() {
        return x;
    }

    public int getz() {
        return z;
    }

    public static int getX() {
        return X;
    }

    public static int getY() {
        return Y;
    }

    public static int getZ() {
        return Z;
    }
    
    public void setBlocks(int blockID,int x,int y,int z) {
        blocks[x][y][z]=new Block(blockID, (this.x << 4) + x, y, (this.z << 4) + z);
    }
    
    public Block getBlock(int x,int y,int z) {
        return blocks[x][y][z];
    }
}
