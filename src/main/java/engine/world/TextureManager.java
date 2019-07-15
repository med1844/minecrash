package engine.world;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Random;

public class TextureManager {
    public static int AIR = 0;
    public static int STONE = 1;
    public static int GRASS = 2;
    public static int DIRT = 3;
    public static int COBBLESTONE = 4;
    public static int PLANKS = 5;
    public static int OAK_SAMPLING = 6;
    public static int BEDROCK = 7;
    public static int FLOWING_WATER = 8;
    public static int STILL_WATER = 9;
    public static int FLOWING_LAVA = 10;
    public static int STILL_LAVA = 11;
    public static int SAND = 12;
    public static int GRAVEL = 13;
    public static int GOLD_ORE = 14;
    public static int IRON_ORE = 15;
    public static int COAL_ORE = 16;
    public static int OAK_WOOD = 17;
    public static int OAK_LEAVES = 18;
    public static int SPONGE = 19;
    public static int GLASS = 20;
    public static int LAPIS_ORE = 21;
    public static int LAPIS_BLOCK = 22;
    public static int DISPENSER = 23;
    public static int SANDSTONE = 24;

    public static int EMPTY = 0;
    public static int SOLID = 1;
    public static int TRANSPARENT = 2;
    public static int MOVABLE = 4; // this means you can move around in this type of block.

    private static int[][] face = {
            {0, 0, 0, 0, 0, 0, EMPTY}, // air
            {0, 0, 0, 0, 0, 0, SOLID}, // stone
            {1, 3, 2, 2, 2, 2, SOLID}, // grass
            {3, 3, 3, 3, 3, 3, SOLID}, // dirt
            {4, 4, 4, 4, 4, 4, SOLID}, // cobblestone
            {5, 5, 5, 5, 5, 5, SOLID}, // planks
            {6, 6, 6, 6, 6, 6, TRANSPARENT | MOVABLE}, // oak sampling
            {7, 7, 7, 7, 7, 7, SOLID}, // bedrock
            {8, 8, 8, 8, 8, 8, TRANSPARENT | MOVABLE}, // flow water
            {9, 9, 9, 9, 9, 9, TRANSPARENT | MOVABLE}, // still water
            {10, 10, 10, 10, 10, 10, TRANSPARENT | MOVABLE}, // flow lava
            {11, 11, 11, 11, 11, 11, TRANSPARENT | MOVABLE}, // still lava
            {12, 12, 12, 12, 12, 12, SOLID}, // sand
            {13, 13, 13, 13, 13, 13, SOLID}, // gravel
            {14, 14, 14, 14, 14, 14, SOLID}, // gold ore
            {15, 15, 15, 15, 15, 15, SOLID}, // iron ore

            {16, 16, 16, 16, 16, 16, SOLID}, // coal ore
            {18, 18, 17, 17, 17, 17, SOLID}, // oak wood
            {19, 19, 19, 19, 19, 19, TRANSPARENT}, // oak leaves
            {20, 20, 20, 20, 20, 20, SOLID}, // sponge
            {21, 21, 21, 21, 21, 21, TRANSPARENT}, // glass
            {22, 22, 22, 22, 22, 22, SOLID}, // lapis ore
            {23, 23, 23, 23, 23, 23, SOLID}, // lapis block
            {0, 0, 0, 0, 0, 0, AIR}, // dispenser, discarded
            {24, 26, 25, 25, 25, 25, SOLID}, // sand stone
    };

    public static Material material;

    private static int pos, tex, norm;

    public static Mesh[] meshes = new Mesh[face.length];

    private static void genFace(int textureID, Vector3f a, Vector3f b, Vector3f c, Vector3f d, Vector3f normalVector,
                                boolean flag, float[] position, float[] textureCoord, float[] normal) {
        float x1 = (textureID / 16) / 16.0f, y1 = (textureID % 16) / 16.0f;
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

    private static float random(float l, float r) {
        Random rand = new Random();
        return l + (r - l) * rand.nextFloat();
    }

    private static void genRandomFace(int textureID, Vector3f a, Vector3f b, Vector3f c, Vector3f d, Vector3f normalVector,
                                      boolean flag, float[] position, float[] textureCoord, float[] normal) {
        float x1 = (textureID / 16) / 16.0f, y1 = (textureID % 16) / 16.0f;
        float x2 = x1 + 1 / 16.0f, y2 = y1 + 1 / 16.0f;
        float x3 = random(x1, x2 - 1 / 64.0f);
        float y3 = random(y1, y2 - 1 / 64.0f);
        float x4 = x3 + 1 / 64.0f;
        float y4 = y3 + 1 / 64.0f;
        Vector2f e = new Vector2f(y3, x3), f = new Vector2f(y4, x3), g = new Vector2f(y3, x4), h = new Vector2f(y4, x4);
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

    private static void genArray(int[] face, float[] position, float[] textureCoord, float[] normal) {
        genFace(face[0], new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0.5f, -0.5f), new Vector3f(-0.5f, 0.5f, -0.5f), new Vector3f(0, 1, 0), false, position, textureCoord, normal);
        genFace(face[1], new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(-0.5f, -0.5f, 0.5f), new Vector3f(0.5f, -0.5f, -0.5f), new Vector3f(0.5f, -0.5f, 0.5f), new Vector3f(0, -1, 0), false, position, textureCoord, normal);
        genFace(face[2], new Vector3f(0.5f, 0.5f, -0.5f), new Vector3f(-0.5f, 0.5f, -0.5f), new Vector3f(0.5f, -0.5f, -0.5f), new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0, 0, -1), false, position, textureCoord, normal);
        genFace(face[3], new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, 0.5f), new Vector3f(0.5f, -0.5f, 0.5f), new Vector3f(-0.5f, -0.5f, 0.5f), new Vector3f(0, 0, 1), true, position, textureCoord, normal);
        genFace(face[4], new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.5f, 0.5f, -0.5f), new Vector3f(0.5f, -0.5f, 0.5f), new Vector3f(0.5f, -0.5f, -0.5f), new Vector3f(1, 0, 0), false, position, textureCoord, normal);
        genFace(face[5], new Vector3f(-0.5f, 0.5f, 0.5f), new Vector3f(-0.5f, 0.5f, -0.5f), new Vector3f(-0.5f, -0.5f, 0.5f), new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(-1, 0, 0), true, position, textureCoord, normal);
    }


    public static int[] getFace(int blockID) {
        if (blockID < face.length) {
            return face[blockID];
        } else {
            return null;
        }
    }

    public static void init() {
        try {
            Texture texture = new Texture("/texture/terrain.png");
            material = new Material(texture, 1f);
            float[] position = new float[36 * 3];
            float[] textureCoord = new float[36 * 2];
            float[] normal = new float[36 * 3];
            int[] indices = new int[36];
            float[] adjacentFaceCount = new float[36];
            for (int i = 0; i < 6; ++i) indices[i] = i;
            for (int i = 0; i < face.length; ++i) {
                norm = tex = pos = 0;
                genArray(face[i], position, textureCoord, normal);
                meshes[i] = new Mesh(position, textureCoord, normal, indices, adjacentFaceCount, material);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] TextureManager.init():\r\n" + e);
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("[INFO] TextureManger.init(): ok");
    }

    public static int getType(int blockID) {
        if (blockID < face.length) {
            return face[blockID][6];
        } else {
            return -1;
        }
    }

    public static void clear() {
        for (Mesh mesh : meshes) {
            mesh.clear();
        }
    }

    public static Mesh newParticleMesh(int blockID) {
        norm = tex = pos = 0;
        float[] position = new float[6 * 3];
        float[] textureCoord = new float[6 * 2];
        float[] normal = new float[6 * 3];
        int[] indices = new int[6];
        float[] adjacentFaceCount = new float[6];
        for (int i = 0; i < 6; ++i) indices[i] = i;
        genRandomFace(face[blockID][(int) (Math.random() * 6)], new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0, 0, 1), true, position, textureCoord, normal);
        return new Mesh(position, textureCoord, normal, indices, adjacentFaceCount, material);
    }
}
