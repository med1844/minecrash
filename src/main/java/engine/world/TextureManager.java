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

    private static int EMPTY = 0;
    private static int SOLID = 1;
    private static int TRANSPARENT = 2;
    private static int MOVABLE = 3; // this means you can move around in this type of block.

    private static int[][] face = {
            {0, 0, 0, 0, 0, 0, EMPTY}, // air
            {0, 0, 0, 0, 0, 0, SOLID}, // stone
            {1, 3, 2, 2, 2, 2, SOLID}, // grass
            {3, 3, 3, 3, 3, 3, SOLID}, // dirt
            {4, 4, 4, 4, 4, 4, SOLID}, // cobblestone
            {5, 5, 5, 5, 5, 5, SOLID}, // planks
            {6, 6, 6, 6, 6, 6, SOLID}, // oak sampling
            {7, 7, 7, 7, 7, 7, SOLID}, // bedrock
            {8, 8, 8, 8, 8, 8, MOVABLE}, // flow water
            {9, 9, 9, 9, 9, 9, MOVABLE}, // still water
            {10, 10, 10, 10, 10, 10, MOVABLE}, // flow lava
            {11, 11, 11, 11, 11, 11, MOVABLE}, // still lava
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
