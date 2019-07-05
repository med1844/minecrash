package engine.world;

import java.util.LinkedList;
import java.util.List;

import engine.graphics.Mesh;
import javafx.util.Pair;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import static engine.world.TextureManager.*;

public class Chunk {

    private int x, z; // the chunk coordinates of current chunk
    private Block[][][] blocks;
    private Mesh mesh;
    private int pos, tex, norm;
    private static final int X = 16;
    private static final int Y = 16;
    private static final int Z = 16;
    private static final int[] dx = {0,  0,  0, 0, 1, -1};
    private static final int[] dy = {1, -1,  0, 0, 0,  0};
    private static final int[] dz = {0,  0, -1, 1, 0,  0};

    public Chunk() {
        this.x = 0;
        this.z = 0;
    }

    public Chunk(int x, int y) {
        this.x = x;
        this.z = y;
        blocks = new Block[X][Y][Z]; // retrieve data through (x, y, z)
    }

    private boolean valid(int x, int y, int z) {
        return 0 <= x && x < X &&
               0 <= y && y < Y &&
               0 <= z && z < Z;
    }

    private void genFace(int textureID, Vector3d a, Vector3d b, Vector3d c, Vector3d d, Vector3d normalVector,
                                boolean flag, float[] position, float[] textureCoord, float[] normal) {
        float x1 = (textureID / 16) / 16.0f, y1 = (textureID % 16) / 16.0f;
        float x2 = x1 + 1 / 16.0f, y2 = y1 + 1 / 16.0f;
        Vector2d e = new Vector2d(y1, x1), f = new Vector2d(y2, x1), g = new Vector2d(y1, x2), h = new Vector2d(y2, x2);
        if (flag) {
            position[pos++] = (float) d.x;
            position[pos++] = (float) d.y;
            position[pos++] = (float) d.z;
            position[pos++] = (float) c.x;
            position[pos++] = (float) c.y;
            position[pos++] = (float) c.z;
            position[pos++] = (float) a.x;
            position[pos++] = (float) a.y;
            position[pos++] = (float) a.z;
            position[pos++] = (float) b.x;
            position[pos++] = (float) b.y;
            position[pos++] = (float) b.z;
            position[pos++] = (float) d.x;
            position[pos++] = (float) d.y;
            position[pos++] = (float) d.z;
            position[pos++] = (float) a.x;
            position[pos++] = (float) a.y;
            position[pos++] = (float) a.z;
            textureCoord[tex++] = (float) h.x;
            textureCoord[tex++] = (float) h.y;
            textureCoord[tex++] = (float) g.x;
            textureCoord[tex++] = (float) g.y;
            textureCoord[tex++] = (float) e.x;
            textureCoord[tex++] = (float) e.y;
            textureCoord[tex++] = (float) f.x;
            textureCoord[tex++] = (float) f.y;
            textureCoord[tex++] = (float) h.x;
            textureCoord[tex++] = (float) h.y;
            textureCoord[tex++] = (float) e.x;
            textureCoord[tex++] = (float) e.y;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
        } else {
            position[pos++] = (float) a.x;
            position[pos++] = (float) a.y;
            position[pos++] = (float) a.z;
            position[pos++] = (float) c.x;
            position[pos++] = (float) c.y;
            position[pos++] = (float) c.z;
            position[pos++] = (float) d.x;
            position[pos++] = (float) d.y;
            position[pos++] = (float) d.z;
            position[pos++] = (float) a.x;
            position[pos++] = (float) a.y;
            position[pos++] = (float) a.z;
            position[pos++] = (float) d.x;
            position[pos++] = (float) d.y;
            position[pos++] = (float) d.z;
            position[pos++] = (float) b.x;
            position[pos++] = (float) b.y;
            position[pos++] = (float) b.z;
            textureCoord[tex++] = (float) e.x;
            textureCoord[tex++] = (float) e.y;
            textureCoord[tex++] = (float) g.x;
            textureCoord[tex++] = (float) g.y;
            textureCoord[tex++] = (float) h.x;
            textureCoord[tex++] = (float) h.y;
            textureCoord[tex++] = (float) e.x;
            textureCoord[tex++] = (float) e.y;
            textureCoord[tex++] = (float) h.x;
            textureCoord[tex++] = (float) h.y;
            textureCoord[tex++] = (float) f.x;
            textureCoord[tex++] = (float) f.y;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
            normal[norm++] = (float) normalVector.x;
            normal[norm++] = (float) normalVector.y;
            normal[norm++] = (float) normalVector.z;
        }
    }

    private void addFace(Block block, int faceID, float[] position, float[] textureCoord, float[] normal) {
        switch (faceID) {
            case 0:
                genFace(block.face[0], new Vector3d(block.x + 1, block.y + 1, block.z + 1), new Vector3d(block.x , block.y + 1, block.z + 1), new Vector3d(block.x + 1, block.y + 1, block.z ), new Vector3d(block.x , block.y + 1, block.z ), new Vector3d(0, 1, 0), false, position, textureCoord, normal);
                break;
            case 1:
                genFace(block.face[1], new Vector3d(block.x , block.y , block.z ), new Vector3d(block.x , block.y , block.z + 1), new Vector3d(block.x + 1, block.y , block.z ), new Vector3d(block.x + 1, block.y , block.z + 1), new Vector3d(0, -1, 0), false, position, textureCoord, normal);
                break;
            case 2:
                genFace(block.face[2], new Vector3d(block.x + 1, block.y + 1, block.z ), new Vector3d(block.x , block.y + 1, block.z ), new Vector3d(block.x + 1, block.y , block.z ), new Vector3d(block.x , block.y , block.z ), new Vector3d(0, 0, -1), false, position, textureCoord, normal);
                break;
            case 3:
                genFace(block.face[3], new Vector3d(block.x + 1, block.y + 1, block.z + 1), new Vector3d(block.x , block.y + 1, block.z + 1), new Vector3d(block.x + 1, block.y , block.z + 1), new Vector3d(block.x , block.y , block.z + 1), new Vector3d(0, 0, 1), true, position, textureCoord, normal);
                break;
            case 4:
                genFace(block.face[4], new Vector3d(block.x + 1, block.y + 1, block.z + 1), new Vector3d(block.x + 1, block.y + 1, block.z ), new Vector3d(block.x + 1, block.y , block.z + 1), new Vector3d(block.x + 1, block.y , block.z ), new Vector3d(1, 0, 0), false, position, textureCoord, normal);
                break;
            case 5:
                genFace(block.face[5], new Vector3d(block.x , block.y + 1, block.z + 1), new Vector3d(block.x , block.y + 1, block.z ), new Vector3d(block.x , block.y , block.z + 1), new Vector3d(block.x , block.y , block.z ), new Vector3d(-1, 0, 0), true, position, textureCoord, normal);
                break;
            default:
                System.err.println("[ERROR] Chunk.addFace(): Invalid faceID!");
                System.exit(-1);
        }
    }

    public void generateMesh(ChunkManager chunkManager) {
        List<Pair<Block, Integer>> l = new LinkedList<>();
        for (int x = 0; x < X; ++x) {
            for (int y = 0; y < Y; ++y) {
                for (int z = 0; z < Z; ++z) {
                    if (blocks[x][y][z].equals(AIR)) continue;
                    for (int d = 0; d < 6; ++d) {
                        int nx = x + dx[d], ny = y + dy[d], nz = z + dz[d];
                        Block temp = null;
                        if (!valid(nx, ny, nz)) {
                            if (d >= 2) {
                                temp = chunkManager.getBlock(
                                        (this.x << 4) + nx,
                                        ny,
                                        (this.z << 4) + nz
                                );
                            }
                        } else {
                            temp = blocks[nx][ny][nz];
                        }
                        if (temp == null || blocks[x][y][z].getType() != temp.getType()) {
                            l.add(new Pair<>(blocks[x][y][z], d));
                        }
                    }
                }
            }
        }
        float[] position = new float[18 * l.size()];
        float[] textureCoord = new float[12 * l.size()];
        float[] normal = new float[18 * l.size()];
        int[] indices = new int[6 * l.size()];
        for (int i = 0; i < indices.length; ++i) indices[i] = i;
        for (Pair<Block, Integer> p : l) {
            addFace(p.getKey(), p.getValue(), position, textureCoord, normal);
        }
        mesh = new Mesh(position, textureCoord, normal, indices, TextureManager.material);
    }

    public void clear() {
        mesh.clear();
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
        blocks[x][y][z] = new Block(blockID, (this.x << 4) + x, y, (this.z << 4) + z);
    }
    
    public Block getBlock(int x,int y,int z) {
        if (valid(x, y, z)) {
            return blocks[x][y][z];
        } else {
            return null;
        }
    }

    public Vector3f getPosition() {
        return new Vector3f(0, 0, 0);
    }

    public void render() {
        mesh.render();
    }
}
