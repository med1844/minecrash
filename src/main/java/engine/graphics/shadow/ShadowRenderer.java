package engine.graphics.shadow;

import engine.Camera;
import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.shaders.Shader;
import engine.maths.Transformations;
import engine.world.Chunk;
import engine.world.Scene;
import engine.world.TextureManager;
import org.joml.Matrix4f;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static engine.graphics.Renderer.*;

public class ShadowRenderer {

    public static final int CASCADE_NUM = 3; // need to change const int CASCADE_NUM in scene.vsh when changing this var
    public static final float[] CASCADE_SPLITS = {Z_FAR / 36.0f, Z_FAR / 9.0f, Z_FAR};

    private Shader depthShader;
    private ShadowBuffer shadowBuffer;
    private List<ShadowCascade> shadowCascades;

    public ShadowRenderer(Shader depthShader) throws Exception {
        this.depthShader = depthShader;
        this.shadowBuffer = new ShadowBuffer();
        this.shadowCascades = new LinkedList<>();

        float zNear = Z_NEAR;
        for (int i = 0; i < CASCADE_NUM; ++i) {
            ShadowCascade shadowCascade = new ShadowCascade(zNear, CASCADE_SPLITS[i]);
            shadowCascades.add(shadowCascade);
            zNear = CASCADE_SPLITS[i];
        }
    }

    private void update(Camera camera, Scene scene) {
        for (int i = 0; i < CASCADE_NUM; ++i) {
            ShadowCascade shadowCascade = shadowCascades.get(i);
            shadowCascade.update(camera, scene.light, i);
        }
    }

    public void render(Camera camera, Scene scene, Transformations transformations) {
//        update(window, transformations.getViewMatrix(camera), scene);
        update(camera, scene);

        glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
        glViewport(0, 0, ShadowBuffer.SHADOW_MAP_WIDTH, ShadowBuffer.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShader.bind();

        for (int i = 0; i < CASCADE_NUM; ++i) {
            ShadowCascade shadowCascade = shadowCascades.get(i);

            depthShader.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjectionMatrix());
            depthShader.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix());

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
            glClear(GL_DEPTH_BUFFER_BIT);

            renderChunks(scene, transformations);
        }

        // Unbind
        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindTextures(int start) {
        shadowBuffer.bindTextures(start);
    }

    private void renderChunks(Scene scene, Transformations transformations) {

//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, TextureManager.material.getTexture().getId());

        for (Chunk chunk : scene.chunkManager.getChunks()) {
            depthShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
            bindTextures(GL_TEXTURE2);
            chunk.renderSolid();
        }
        for (Chunk chunk : scene.chunkManager.getChunks()) {
            depthShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
            bindTextures(GL_TEXTURE2);
            chunk.renderTransparencies();
        }
    }

    public List<ShadowCascade> getShadowCascades() {
        return shadowCascades;
    }

    public void clear() {
        if (shadowBuffer != null) shadowBuffer.clear();
        if (depthShader != null) depthShader.clear();
    }
}
