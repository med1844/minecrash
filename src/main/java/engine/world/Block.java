package engine.world;

import org.joml.Vector3f;

public class Block {
    private int blockID;
    private int type;
    public int x, y, z; // stores ABSOLUTE coordinate in the world
    public int[] face;

    public Block(int blockID, int x, int y, int z) {
        this.blockID = blockID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = TextureManager.getType(blockID);
        this.face = TextureManager.getFace(blockID);
    }

    public Vector3f getPosition() {
        return new Vector3f(x, y, z);
    }

    public boolean equals(int ID) {
        return blockID == ID;
    }

    public int getType() {
        return this.type;
    }

    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]: " + blockID;
    }
}
