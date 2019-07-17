package engine.graphics.HUD;

import engine.IO.Window;
import engine.graphics.Material;
import engine.graphics.Mesh;
import engine.graphics.Texture;
import org.joml.Matrix4f;

public class Inventory {

    private Mesh inventory;
    private int width, height;

    public Inventory() throws Exception {
        Material material = new Material(new Texture("/texture/widget.png"), 0);
        width = 182 << 2;
        height = 22 << 2;
        float[] position = {
                0, 0, 0,
                0, height, 0,
                width, 0, 0,
                width, height, 0
        };
        float[] textureCoord = {
                0, 0,
                0, 22 / 256f,
                182 / 256f, 0,
                182 / 256f, 22 / 256f
        };
        float[] normal = {
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1
        };
        int[] indices = {
                0, 1, 3, 0, 3, 2
        };
        float[] adjacentFaceCnt = {
                0, 0, 0, 0, 0, 0
        };
        inventory = new Mesh(position, textureCoord, normal, indices, adjacentFaceCnt, material);
    }

    public void render() {
        inventory.getMaterial().getTexture().bind();
        inventory.render();
    }

    public void clear() {
        inventory.clear();
    }

    public Matrix4f getModelMatrix(Window window) {
        return new Matrix4f().identity().translate((window.getWidth() - width) >> 1, (window.getHeight() - height), 0);
    }

}
