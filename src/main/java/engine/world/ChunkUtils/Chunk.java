package engine.world.ChunkUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import engine.graphics.Mesh;
import engine.maths.FrustumCullFilter;
import engine.world.Block;
import engine.world.TextureManager;
import javafx.util.Pair;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static engine.world.TextureManager.*;

public class Chunk {

    private int x, z; // the chunk coordinates of current chunk
    private Block[][][] blocks;
    private Mesh[] solid, transparencies;
    private boolean[] isEmptySolid;
    private boolean[] isEmptyTransparencies;
    private int pos, tex, norm, adj;
    private static final int X = 16;
    private static final int Y = 256;
    private static final int Z = 16;
    private static final int[] dx = {0, 0, 0, 0, 1, -1};
    private static final int[] dy = {1, -1, 0, 0, 0, 0};
    private static final int[] dz = {0, 0, -1, 1, 0, 0};
    private Map<Pair<Vector3f, Integer>, Integer> m;

    public Chunk(int x, int y) {
        this.x = x;
        this.z = y;
        blocks = new Block[X][Y][Z]; // retrieve data through (x, y, z)
        m = new HashMap<>();
        solid = new Mesh[Y >> 4];
        transparencies = new Mesh[Y >> 4];
        isEmptySolid = new boolean[Y >> 4];
        isEmptyTransparencies = new boolean[Y >> 4];
    }

    private boolean valid(int x, int y, int z) {
        return 0 <= x && x < X &&
                0 <= y && y < Y &&
                0 <= z && z < Z;
    }

    private void addCount(Vector3f vertex, int d) {
        Pair<Vector3f, Integer> key = new Pair<>(vertex, d);
        if (m.containsKey(key)) m.put(key, m.get(key) + 1);
        else m.put(key, 1);
    }

    private float processAO(float rawFaceCnt) {
        return rawFaceCnt != 0 ? 0.45f : 1;
    }

    private void genFace(int textureID, Vector3f a, Vector3f b, Vector3f c, Vector3f d, Vector3f normalVector,
                         boolean flag, float[] position, float[] textureCoord, float[] normal, float[] adjacentFaceCount, int n) {
        float x1 = (textureID >> 4) / 16.0f, y1 = (textureID % 16) / 16.0f;
        float x2 = x1 + 1 / 16.0f, y2 = y1 + 1 / 16.0f;
        Vector2f e = new Vector2f(y1, x1), f = new Vector2f(y2, x1), g = new Vector2f(y1, x2), h = new Vector2f(y2, x2);
        if (flag) {
            position[pos++] = d.x;
            position[pos++] = d.y;
            position[pos++] = d.z;
            position[pos++] = c.x;
            position[pos++] = c.y;
            position[pos++] = c.z;
            position[pos++] = a.x;
            position[pos++] = a.y;
            position[pos++] = a.z;
            position[pos++] = b.x;
            position[pos++] = b.y;
            position[pos++] = b.z;
            position[pos++] = d.x;
            position[pos++] = d.y;
            position[pos++] = d.z;
            position[pos++] = a.x;
            position[pos++] = a.y;
            position[pos++] = a.z;
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(d, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(c, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(a, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(b, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(d, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(a, n), 0));
            textureCoord[tex++] = h.x;
            textureCoord[tex++] = h.y;
            textureCoord[tex++] = g.x;
            textureCoord[tex++] = g.y;
            textureCoord[tex++] = e.x;
            textureCoord[tex++] = e.y;
            textureCoord[tex++] = f.x;
            textureCoord[tex++] = f.y;
            textureCoord[tex++] = h.x;
            textureCoord[tex++] = h.y;
            textureCoord[tex++] = e.x;
            textureCoord[tex++] = e.y;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
        } else {
            position[pos++] = a.x;
            position[pos++] = a.y;
            position[pos++] = a.z;
            position[pos++] = c.x;
            position[pos++] = c.y;
            position[pos++] = c.z;
            position[pos++] = d.x;
            position[pos++] = d.y;
            position[pos++] = d.z;
            position[pos++] = a.x;
            position[pos++] = a.y;
            position[pos++] = a.z;
            position[pos++] = d.x;
            position[pos++] = d.y;
            position[pos++] = d.z;
            position[pos++] = b.x;
            position[pos++] = b.y;
            position[pos++] = b.z;
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(a, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(c, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(d, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(a, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(d, n), 0));
            adjacentFaceCount[adj++] = processAO(m.getOrDefault(new Pair<>(b, n), 0));
            textureCoord[tex++] = e.x;
            textureCoord[tex++] = e.y;
            textureCoord[tex++] = g.x;
            textureCoord[tex++] = g.y;
            textureCoord[tex++] = h.x;
            textureCoord[tex++] = h.y;
            textureCoord[tex++] = e.x;
            textureCoord[tex++] = e.y;
            textureCoord[tex++] = h.x;
            textureCoord[tex++] = h.y;
            textureCoord[tex++] = f.x;
            textureCoord[tex++] = f.y;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
            normal[norm++] = normalVector.x;
            normal[norm++] = normalVector.y;
            normal[norm++] = normalVector.z;
        }
    }

    public void addFace(Block block, int faceID, float[] position, float[] textureCoord, float[] normal, float[] adjacentFaceCount) {
        int x = block.x & 15, y = block.y, z = block.z & 15;
        switch (faceID) {
            case 0:
                genFace(block.face[0], new Vector3f(x + 1, y + 1, z + 1), new Vector3f(x, y + 1, z + 1), new Vector3f(x + 1, y + 1, z), new Vector3f(x, y + 1, z), new Vector3f(0, 1, 0), false, position, textureCoord, normal, adjacentFaceCount, 0);
                break;
            case 1:
                genFace(block.face[1], new Vector3f(x, y, z), new Vector3f(x, y, z + 1), new Vector3f(x + 1, y, z), new Vector3f(x + 1, y, z + 1), new Vector3f(0, -1, 0), false, position, textureCoord, normal, adjacentFaceCount, 1);
                break;
            case 2:
                genFace(block.face[2], new Vector3f(x + 1, y + 1, z), new Vector3f(x, y + 1, z), new Vector3f(x + 1, y, z), new Vector3f(x, y, z), new Vector3f(0, 0, -1), false, position, textureCoord, normal, adjacentFaceCount, 2);
                break;
            case 3:
                genFace(block.face[3], new Vector3f(x + 1, y + 1, z + 1), new Vector3f(x, y + 1, z + 1), new Vector3f(x + 1, y, z + 1), new Vector3f(x, y, z + 1), new Vector3f(0, 0, 1), true, position, textureCoord, normal, adjacentFaceCount, 3);
                break;
            case 4:
                genFace(block.face[4], new Vector3f(x + 1, y + 1, z + 1), new Vector3f(x + 1, y + 1, z), new Vector3f(x + 1, y, z + 1), new Vector3f(x + 1, y, z), new Vector3f(1, 0, 0), false, position, textureCoord, normal, adjacentFaceCount, 4);
                break;
            case 5:
                genFace(block.face[5], new Vector3f(x, y + 1, z + 1), new Vector3f(x, y + 1, z), new Vector3f(x, y, z + 1), new Vector3f(x, y, z), new Vector3f(-1, 0, 0), true, position, textureCoord, normal, adjacentFaceCount, 5);
                break;
            default:
                System.err.println("[ERROR] Chunk.addFace(): Invalid faceID!");
                System.exit(-1);
        }
    }

    private void genAO(int x, int y, int z, int nx, int ny, int nz, Vector3f a, Vector3f b, ChunkManager chunkManager, int d) {
        Block source = valid(x, y, z) ? blocks[x][y][z] : chunkManager.getBlock((this.x << 4) + x, y, (this.z << 4) + z);
        Block target = valid(nx, ny, nz) ? blocks[nx][ny][nz] : chunkManager.getBlock((this.x << 4) + nx, ny, (this.z << 4) + nz);
        if (target == null || source == null || source.getType() == target.getType()) return;
        addCount(a, d);
        addCount(b, d);
    }

    private void genAO(int x, int y, int z, int nx, int ny, int nz, Vector3f a, ChunkManager chunkManager, int d) {
        Block source = valid(x, y, z) ? blocks[x][y][z] : chunkManager.getBlock((this.x << 4) + x, y, (this.z << 4) + z);
        Block target = valid(nx, ny, nz) ? blocks[nx][ny][nz] : chunkManager.getBlock((this.x << 4) + nx, ny, (this.z << 4) + nz);
        if (target == null || source == null || source.getType() == target.getType()) return;
        addCount(a, d);
    }
    private void addAO(int x, int y, int z, int d, ChunkManager chunkManager) {
        switch (d) {
            case 0: // UP
                genAO(x, y + 1, z, x - 1, y + 1, z, new Vector3f(x, y + 1, z), new Vector3f(x, y + 1, z + 1), chunkManager, d);
                genAO(x, y + 1, z, x + 1, y + 1, z, new Vector3f(x + 1, y + 1, z), new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                genAO(x, y + 1, z, x, y + 1, z - 1, new Vector3f(x, y + 1, z), new Vector3f(x + 1, y + 1, z), chunkManager, d);
                genAO(x, y + 1, z, x, y + 1, z + 1, new Vector3f(x, y + 1, z + 1), new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                genAO(x, y + 1, z, x - 1, y + 1, z - 1, new Vector3f(x, y + 1, z), chunkManager, d);
                genAO(x, y + 1, z, x - 1, y + 1, z + 1, new Vector3f(x, y + 1, z + 1), chunkManager, d);
                genAO(x, y + 1, z, x + 1, y + 1, z - 1, new Vector3f(x + 1, y + 1, z), chunkManager, d);
                genAO(x, y + 1, z, x + 1, y + 1, z + 1, new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                break;
            case 1: // DOWN
                genAO(x, y - 1, z, x - 1, y - 1, z, new Vector3f(x, y, z), new Vector3f(x, y, z + 1), chunkManager, d);
                genAO(x, y - 1, z, x + 1, y - 1, z, new Vector3f(x + 1, y, z), new Vector3f(x + 1, y, z + 1), chunkManager, d);
                genAO(x, y - 1, z, x, y - 1, z - 1, new Vector3f(x, y, z), new Vector3f(x + 1, y, z), chunkManager, d);
                genAO(x, y - 1, z, x, y - 1, z + 1, new Vector3f(x, y, z + 1), new Vector3f(x + 1, y, z + 1), chunkManager, d);
                genAO(x, y - 1, z, x - 1, y - 1, z - 1, new Vector3f(x, y, z), chunkManager, d);
                genAO(x, y - 1, z, x - 1, y - 1, z + 1, new Vector3f(x, y, z + 1), chunkManager, d);
                genAO(x, y - 1, z, x + 1, y - 1, z - 1, new Vector3f(x + 1, y, z), chunkManager, d);
                genAO(x, y - 1, z, x + 1, y - 1, z + 1, new Vector3f(x + 1, y, z + 1), chunkManager, d);
                break;
            case 2: // LEFT
                genAO(x, y, z - 1, x - 1, y, z - 1, new Vector3f(x, y, z), new Vector3f(x, y + 1, z), chunkManager, d);
                genAO(x, y, z - 1, x + 1, y, z - 1, new Vector3f(x + 1, y, z), new Vector3f(x + 1, y + 1, z), chunkManager, d);
                genAO(x, y, z - 1, x, y - 1, z - 1, new Vector3f(x, y, z), new Vector3f(x + 1, y, z), chunkManager, d);
                genAO(x, y, z - 1, x, y + 1, z - 1, new Vector3f(x, y + 1, z), new Vector3f(x + 1, y + 1, z), chunkManager, d);
                genAO(x, y, z - 1, x - 1, y - 1, z - 1 ,new Vector3f(x, y, z), chunkManager, d);
                genAO(x, y, z - 1, x + 1, y - 1, z - 1 ,new Vector3f(x + 1, y, z), chunkManager, d);
                genAO(x, y, z - 1, x - 1, y + 1, z - 1 ,new Vector3f(x, y + 1, z), chunkManager, d);
                genAO(x, y, z - 1, x + 1, y + 1, z - 1 ,new Vector3f(x + 1, y + 1, z), chunkManager, d);
                break;
            case 3: // RIGHT
                genAO(x, y, z + 1, x - 1, y, z + 1, new Vector3f(x, y, z + 1), new Vector3f(x, y + 1, z + 1), chunkManager, d);
                genAO(x, y, z + 1, x + 1, y, z + 1, new Vector3f(x + 1, y, z + 1), new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                genAO(x, y, z + 1, x, y - 1, z + 1, new Vector3f(x, y, z + 1), new Vector3f(x + 1, y, z + 1), chunkManager, d);
                genAO(x, y, z + 1, x, y + 1, z + 1, new Vector3f(x, y + 1, z + 1), new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                genAO(x, y, z + 1, x - 1, y - 1, z + 1 ,new Vector3f(x, y, z + 1), chunkManager, d);
                genAO(x, y, z + 1, x + 1, y - 1, z + 1 ,new Vector3f(x + 1, y, z + 1), chunkManager, d);
                genAO(x, y, z + 1, x - 1, y + 1, z + 1 ,new Vector3f(x, y + 1, z + 1), chunkManager, d);
                genAO(x, y, z + 1, x + 1, y + 1, z + 1 ,new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                break;
            case 4: // FRONT
                genAO(x + 1, y, z, x + 1, y, z - 1, new Vector3f(x + 1, y, z), new Vector3f(x + 1, y + 1, z), chunkManager, d);
                genAO(x + 1, y, z, x + 1, y, z + 1, new Vector3f(x + 1, y, z + 1), new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                genAO(x + 1, y, z, x + 1, y - 1, z, new Vector3f(x + 1, y, z), new Vector3f(x + 1, y, z + 1), chunkManager, d);
                genAO(x + 1, y, z, x + 1, y + 1, z, new Vector3f(x + 1, y + 1, z), new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                genAO(x + 1, y, z, x + 1, y - 1, z - 1, new Vector3f(x + 1, y, z), chunkManager, d);
                genAO(x + 1, y, z, x + 1, y - 1, z + 1, new Vector3f(x + 1, y, z + 1), chunkManager, d);
                genAO(x + 1, y, z, x + 1, y + 1, z - 1, new Vector3f(x + 1, y + 1, z), chunkManager, d);
                genAO(x + 1, y, z, x + 1, y + 1, z + 1, new Vector3f(x + 1, y + 1, z + 1), chunkManager, d);
                break;
            case 5: // BACK
                genAO(x - 1, y, z, x - 1, y, z - 1, new Vector3f(x, y, z), new Vector3f(x, y + 1, z), chunkManager, d);
                genAO(x - 1, y, z, x - 1, y, z + 1, new Vector3f(x, y, z + 1), new Vector3f(x, y + 1, z + 1), chunkManager, d);
                genAO(x - 1, y, z, x - 1, y - 1, z, new Vector3f(x, y, z), new Vector3f(x, y, z + 1), chunkManager, d);
                genAO(x - 1, y, z, x - 1, y + 1, z, new Vector3f(x, y + 1, z), new Vector3f(x, y + 1, z + 1), chunkManager, d);
                genAO(x - 1, y, z, x - 1, y - 1, z - 1, new Vector3f(x, y, z), chunkManager, d);
                genAO(x - 1, y, z, x - 1, y - 1, z + 1, new Vector3f(x, y, z + 1), chunkManager, d);
                genAO(x - 1, y, z, x - 1, y + 1, z - 1, new Vector3f(x, y + 1, z), chunkManager, d);
                genAO(x - 1, y, z, x - 1, y + 1, z + 1, new Vector3f(x, y + 1, z + 1), chunkManager, d);
                break;
            default:
                System.err.println("[ERROR Chunk.addAO()]: Invalid faceID!");
        }
    }

    private boolean generatePartMesh(ChunkManager chunkManager, int index, int type, Mesh[] target) {
        List<Pair<Block, Integer>> l = new LinkedList<>();
        m.clear();
        pos = tex = norm = adj = 0;
        for (int x = 0; x < X; ++x) {
            for (int y = index << 4; y < (index + 1) << 4; ++y) {
                for (int z = 0; z < Z; ++z) {
                    if (blocks[x][y][z].equals(AIR)) continue;
                    if ((blocks[x][y][z].getType() & type) != 0) {
                        for (int d = 0; d < 6; ++d) {
                            int nx = x + dx[d], ny = y + dy[d], nz = z + dz[d];
                            Block temp;
                            if (!valid(nx, ny, nz)) {
                                temp = chunkManager.getBlock(
                                        (this.x << 4) + nx,
                                        ny,
                                        (this.z << 4) + nz
                                );
                            } else {
                                temp = blocks[nx][ny][nz];
                            }
//                            if (temp == null) continue;
                            if ((temp == null) || (blocks[x][y][z].getType() & 3) != (temp.getType() & 3)) {
                                l.add(new Pair<>(blocks[x][y][z], d));
                                addAO(x, y, z, d, chunkManager);
                            }
                        }
                    }
                }
            }
        }
        float[] position = new float[18 * l.size()];
        float[] textureCoord = new float[12 * l.size()];
        float[] normal = new float[18 * l.size()];
        float[] adjacentFaceCount = new float[6 * l.size()];
        int[] indices = new int[6 * l.size()];
        for (int i = 0; i < indices.length; ++i) indices[i] = i;
        for (Pair<Block, Integer> p : l) {
            addFace(p.getKey(), p.getValue(), position, textureCoord, normal, adjacentFaceCount);
        }
        target[index] = new Mesh(position, textureCoord, normal, indices, adjacentFaceCount, TextureManager.material);
        return l.isEmpty();
    }

    public void generateMesh(ChunkManager chunkManager) {
        for (int i = 0; i < (Y >> 4); ++i) {
            isEmptySolid[i] = generatePartMesh(chunkManager, i, SOLID, solid);
            isEmptyTransparencies[i] = generatePartMesh(chunkManager, i, TRANSPARENT, transparencies);
        }
    }

    public void buildSolidMesh(int index, float[] position, float[] textureCoord, float[] normal, float[] adjacentFaceCount, int[] indices, boolean flag) {
        if (flag) {
            solid[index] = new Mesh(position, textureCoord, normal, indices, adjacentFaceCount, material);
            isEmptySolid[index] = false;
        } else {
            isEmptySolid[index] = true;
        }
    }

    public void buildTransparentMesh(int index, float[] position, float[] textureCoord, float[] normal, float[] adjacentFaceCount, int[] indices, boolean flag) {
        if (flag) {
            transparencies[index] = new Mesh(position, textureCoord, normal, indices, adjacentFaceCount, material);
            isEmptyTransparencies[index] = false;
        } else {
            isEmptyTransparencies[index] = true;
        }
    }

    public List<Pair<Block, Integer>> generatePartMeshL(ChunkManager chunkManager, int i, int type) {
        List<Pair<Block, Integer>> l = new LinkedList<>();
        m.clear();
        pos = tex = norm = adj = 0;
        for (int x = 0; x < X; ++x) {
            for (int y = i << 4; y < (i + 1) << 4; ++y) {
                for (int z = 0; z < Z; ++z) {
                    if (blocks[x][y][z].equals(AIR)) continue;
                    if ((blocks[x][y][z].getType() & type) != 0) {
                        for (int d = 0; d < 6; ++d) {
                            int nx = x + dx[d], ny = y + dy[d], nz = z + dz[d];
                            Block temp;
                            if (!valid(nx, ny, nz)) {
                                temp = chunkManager.getBlock(
                                        (this.x << 4) + nx,
                                        ny,
                                        (this.z << 4) + nz
                                );
                            } else {
                                temp = blocks[nx][ny][nz];
                            }
//                            if (temp == null) continue;
                            if ((temp == null) || (blocks[x][y][z].getType() & 3) != (temp.getType() & 3)) {
                                l.add(new Pair<>(blocks[x][y][z], d));
                                addAO(x, y, z, d, chunkManager);
                            }
                        }
                    }
                }
            }
        }
        return l;
    }

    public void updateMesh(int i, ChunkManager chunkManager) {
        if (0 <= i && i < (Y >> 4)) {
            isEmptySolid[i] = generatePartMesh(chunkManager, i, SOLID, solid);
            isEmptyTransparencies[i] = generatePartMesh(chunkManager, i, TRANSPARENT, transparencies);
        }
    }

    public void clear() {
        for (int i = 0; i < (Y >> 4); ++i) {
            if (solid[i] != null) {
                solid[i].clear();
            }
            if (transparencies[i] != null) {
                transparencies[i].clear();
            }
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

    public void setBlock(int blockID, int x, int y, int z) {
        if (0 <= x && x < X && 0 <= y && y < Y  && 0 <= z && z < Z) {
            blocks[x][y][z] = new Block(blockID, (this.x << 4) + x, y, (this.z << 4) + z);
        }
    }
    
    public Block getBlock(int x, int y, int z) {
        if (valid(x, y, z)) {
            return blocks[x][y][z];
        } else {
            return null;
        }
    }

    public Vector3f getPosition() {
        return new Vector3f(x << 4, 0, z << 4);
    }

    public void renderSolid(FrustumCullFilter frustumCullFilter) {
        for (int i = 0; i < (Y >> 4); ++i) {
            if (isEmptySolid[i]) continue;
            if (frustumCullFilter == null || frustumCullFilter.insideFrustum((x << 4) + (X >> 1), (i << 4) + 8, (z << 4) + (Z >> 1), 13.856406460551018f)) {
                if (solid[i] != null) {
                    solid[i].render();
                }
            }
        }
    }

    public void renderTransparencies(FrustumCullFilter frustumCullFilter) {
        for (int i = 0; i < (Y >> 4); ++i) {
            if (isEmptyTransparencies[i]) continue;
            if (frustumCullFilter == null || frustumCullFilter.insideFrustum((x << 4) + (X >> 1), (i << 4) + 8, (z << 4) + (Z >> 1), 13.856406460551018f)) {
                if (transparencies[i] != null) {
                    transparencies[i].render();
                }
            }
        }
    }

}
