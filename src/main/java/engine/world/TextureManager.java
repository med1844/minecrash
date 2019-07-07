package engine.world;

import engine.graphics.Material;
import engine.graphics.Texture;

public class TextureManager {
    public static int AIR = 0;
    public static int STONE = 1;
    public static int GRASS = 2;
    public static int DIRT = 3;
    public static int COBBLESTONE = 4;
    public static int PLANKS = 5;

    private static int EMPTY = 0;
    private static int SOLID = 1;
    private static int GLASS = 2;
    private static int WATER = 3;

    private static int[][] face = {
            {0, 0, 0, 0, 0, 0, EMPTY}, // air
            {0, 0, 0, 0, 0, 0, SOLID}, // stone
            {1, 3, 2, 2, 2, 2, SOLID}, // grass
            {3, 3, 3, 3, 3, 3, SOLID}, // dirt
            {4, 4, 4, 4, 4, 4, SOLID}, // cobblestone
            {5, 5, 5, 5, 5, 5, SOLID}, // planks
    };

    public static Material material;

    public TextureManager() {
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
        } catch (Exception e) {
            System.err.println("[ERROR] TextureManager.init():\r\n" + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static int getType(int blockID) {
        if (blockID < face.length) {
            return face[blockID][6];
        } else {
            return -1;
        }
    }
}
