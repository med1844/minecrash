package engine.world;

import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Texture;
import org.joml.Vector3d;
import org.joml.Vector2d;

public class TextureManager {
    public static int AIR = 0;
    public static int STONE = 1;
    public static int GRASS = 2;
    public static int DIRT = 3;
    public static int COBBLESTONE = 4;
    public static int PLANKS = 5;
    public static Mesh[] meshes;

    private static float[] position = new float[36 * 3];
    private static float[] textureCoord = new float[36 * 2];
    private static float[] normal = new float[36 * 3];
    private static int[] indices = new int[36];
    private static int pos, tex, norm;

    public TextureManager() {
    }

    private static void genFace(int textureID, Vector3d a, Vector3d b, Vector3d c, Vector3d d, Vector3d normalVector) {
        float x1 = (textureID / 16) / 16.0f, y1 = (textureID % 16) / 16.0f;
        float x2 = x1 + 1 / 16.0f, y2 = y1 + 1 / 16.0f;
        Vector2d e = new Vector2d(y1, x1), f = new Vector2d(y2, x1), g = new Vector2d(y1, x2), h = new Vector2d(y2, x2);
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
        position[pos++] = (float) b.x;
        position[pos++] = (float) b.y;
        position[pos++] = (float) b.z;
        position[pos++] = (float) d.x;
        position[pos++] = (float) d.y;
        position[pos++] = (float) d.z;
        textureCoord[tex++] = (float) e.x;
        textureCoord[tex++] = (float) e.y;
        textureCoord[tex++] = (float) g.x;
        textureCoord[tex++] = (float) g.y;
        textureCoord[tex++] = (float) h.x;
        textureCoord[tex++] = (float) h.y;
        textureCoord[tex++] = (float) e.x;
        textureCoord[tex++] = (float) e.y;
        textureCoord[tex++] = (float) f.x;
        textureCoord[tex++] = (float) f.y;
        textureCoord[tex++] = (float) h.x;
        textureCoord[tex++] = (float) h.y;
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

    private static void genArray(int[] face) {
        genFace(face[0], new Vector3d(-0.5, 0.5, -0.5), new Vector3d(-0.5, 0.5, 0.5), new Vector3d(0.5, 0.5, -0.5), new Vector3d(0.5, 0.5, 0.5), new Vector3d(0, 1, 0));
        genFace(face[1], new Vector3d(-0.5, -0.5, -0.5), new Vector3d(-0.5, -0.5, 0.5), new Vector3d(0.5, -0.5, -0.5), new Vector3d(0.5, -0.5, 0.5), new Vector3d(0, -1, 0));
        genFace(face[2], new Vector3d(0.5, 0.5, -0.5), new Vector3d(-0.5, 0.5, -0.5), new Vector3d(0.5, -0.5, -0.5), new Vector3d(-0.5, -0.5, -0.5), new Vector3d(0, 0, -1));
        genFace(face[3], new Vector3d(0.5, 0.5, 0.5), new Vector3d(-0.5, 0.5, 0.5), new Vector3d(0.5, -0.5, 0.5), new Vector3d(-0.5, -0.5, 0.5), new Vector3d(0, 0, 1));
        genFace(face[4], new Vector3d(0.5, 0.5, 0.5), new Vector3d(0.5, 0.5, -0.5), new Vector3d(0.5, -0.5, 0.5), new Vector3d(0.5, -0.5, -0.5), new Vector3d(1, 0, 0));
        genFace(face[5], new Vector3d(-0.5, 0.5, 0.5), new Vector3d(-0.5, 0.5, -0.5), new Vector3d(-0.5, -0.5, 0.5), new Vector3d(-0.5, -0.5, -0.5), new Vector3d(-1, 0, 0));
    }

    public static void init() {
        try {
            Texture texture = new Texture("/texture/terrain.png");
            Material material = new Material(texture, 1f);
            meshes = new Mesh[10];
            int[][] face = {
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {1, 3, 2, 2, 2, 2},
                    {3, 3, 3, 3, 3, 3},
                    {4, 4, 4, 4, 4, 4},
                    {5, 5, 5, 5, 5, 5}
            };
            for (int i = 0; i < 36; ++i) indices[i] = i;
            for (int i = 0; i < 6; ++i) {
                norm = tex = pos = 0;
                genArray(face[i]);
                meshes[i] = new Mesh(position, textureCoord, normal, indices, material);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] TextureManager.init():\r\n" + e);
            System.exit(-1);
        }
    }
}
