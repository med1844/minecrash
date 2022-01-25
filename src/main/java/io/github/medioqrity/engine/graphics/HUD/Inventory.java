package io.github.medioqrity.engine.graphics.HUD;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

import io.github.medioqrity.engine.IO.Window;
import io.github.medioqrity.engine.graphics.Material;
import io.github.medioqrity.engine.graphics.Mesh;
import io.github.medioqrity.engine.graphics.Texture;
import io.github.medioqrity.engine.graphics.shaders.Shader;
import io.github.medioqrity.engine.world.TextureManager;

public class Inventory {

    private Mesh inventory;
    private Mesh selection; // the selection box;
    private Material inventoryMaterial;
    private int inv_width, inv_height;
    private int sel_width, sel_height;
    private int selection_i = 0;
    private Matrix4f orthoProjection;
    private int[] items;

    public Inventory() throws Exception {
        inventoryMaterial = new Material(new Texture("/texture/widget.png"), 0);
        items = new int[9];
        for (int i = 1; i < 9; ++i) items[i] = i + 12;
        inv_width = 182 << 2;
        inv_height = 22 << 2;
        sel_width = 24 << 2;
        sel_height = 24 << 2;
        float[] position_inv = {
                0, 0, 0,
                0, inv_height, 0,
                inv_width, 0, 0,
                inv_width, inv_height, 0
        };
        float[] textureCoord_inv = {
                0, 0,
                0, 22 / 256f,
                182 / 256f, 0,
                182 / 256f, 22 / 256f
        };
        float[] normal_inv = {
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1
        };
        int[] indices_inv = {
                0, 1, 3, 0, 3, 2
        };
        float[] adjacentFaceCnt_inv = {
                0, 0, 0, 0, 0, 0
        };
        inventory = new Mesh(position_inv, textureCoord_inv, normal_inv, indices_inv, adjacentFaceCnt_inv, inventoryMaterial);

        float[] position_sel = {
                0, 0, 0,
                0, sel_height, 0,
                sel_width, 0, 0,
                sel_width, sel_height, 0
        };
        float[] textureCoord_sel = {
                0, 22 / 256f,
                0, 46 / 256f,
                24 / 256f, 22 / 256f,
                24 / 256f, 46 / 256f
        };
        selection = new Mesh(position_sel, textureCoord_sel, normal_inv, indices_inv, adjacentFaceCnt_inv, inventoryMaterial);
        orthoProjection = new Matrix4f();
    }

    public void render(Window window, Shader HUDShader) {
        glDisable(GL_DEPTH_TEST);

        orthoProjection.setOrtho2D(0, window.getWidth(), window.getHeight(), 0);
        HUDShader.setUniform("projectionMatrix", orthoProjection);
        HUDShader.setUniform("texture_sampler", 1);

        glActiveTexture(GL_TEXTURE1);
        inventoryMaterial.getTexture().bind();

        HUDShader.setUniform("modelMatrix", getInventoryModelMatrix(window));
        inventory.render();

        HUDShader.setUniform("modelMatrix", getSelectionModelMatrix(window));
        selection.render();

        glActiveTexture(GL_TEXTURE0);
        TextureManager.material.getTexture().bind();

        HUDShader.setUniform("texture_sampler", 0);
        for (int i = 0; i < 9; ++i) {
            if (items[i] != 0) {
                Mesh mesh = TextureManager.getBlockMesh(items[i]);
                HUDShader.setUniform("modelMatrix", getSlotModelMatrix(window, i));
                mesh.render();
            }
        }

        glEnable(GL_DEPTH_TEST);
    }

    public void clear() {
        inventory.clear();
        selection.clear();
    }

    public Matrix4f getInventoryModelMatrix(Window window) {
        return new Matrix4f().identity().translate((window.getWidth() - inv_width) >> 1, (window.getHeight() - inv_height), 0);
    }

    public Matrix4f getSelectionModelMatrix(Window window) {
        return new Matrix4f().identity().translate(((window.getWidth() - inv_width) >> 1) - (1 << 2) + selection_i * (20 << 2), (window.getHeight() - inv_height) - (1 << 2), 0);
    }

    public Matrix4f getSlotModelMatrix(Window window, int i) {
        return new Matrix4f().identity().translate(((window.getWidth() - inv_width) >> 1) + (3 << 2) + i * (20 << 2), (window.getHeight() - inv_height) + (3 << 2), 0);
    }

    /**
     * move selection box to a new place.
     * @param delta the number of steps to move.
     *              move to smaller indices when delta is negative.
     */
    public void move(int delta) {
        selection_i += delta;
        selection_i %= 9;
        if (selection_i < 0) selection_i += 9;
    }

    public void set(int blockID) {
        items[selection_i] = blockID;
    }

    public int get() {
        return items[selection_i];
    }

    public void drop() {
        items[selection_i] = 0;
    }

}
