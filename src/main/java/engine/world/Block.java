package engine.world;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class Block {
    private int blockID;
    private int x, y, z;
    private Mesh mesh;

    public Block(int blockID, int x, int y, int z) {
        this.mesh = TextureManager.meshes[blockID];
        this.blockID = blockID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void render() {
        mesh.render();
    }

    public void clear() {
        mesh.clear();
    }

    public Vector3f getPosition() {
        return new Vector3f(x, y, z);
    }

    public boolean equals(int ID) {
        return blockID == ID;
    }
}
