package engine.world;

import engine.graphics.Mesh;
import engine.graphics.Texture;

public class TextureManager {
    public static int AIR = 0;
    public static int STONE = 1;
    public static int GRASS = 2;
    public static int DIRT = 3;
    public static int COBBLESTONE = 4;
    public static int PLANKS = 5;
    public static Mesh[] meshes;
    public static Texture texture;

    public TextureManager() {
    }

    public void init() {
        try {
            texture = new Texture("/texture/terrain.png");
        } catch (Exception e) {
            System.err.println("[ERROR] TextureManager().<init>():\r\n" + e);
        }
    }
}
