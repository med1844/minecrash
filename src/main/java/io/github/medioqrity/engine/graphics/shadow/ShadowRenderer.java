package io.github.medioqrity.engine.graphics.shadow;

import io.github.medioqrity.engine.Camera;
import io.github.medioqrity.engine.graphics.shaders.Shader;
import io.github.medioqrity.engine.maths.FrustumCullFilter;
import io.github.medioqrity.engine.maths.Transformations;
import io.github.medioqrity.engine.world.ChunkUtils.Chunk;
import io.github.medioqrity.engine.world.Scene;
import org.joml.Matrix4f;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static io.github.medioqrity.engine.graphics.Renderer.*;

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

        for (int i = 0; i < CASCADE_NUM; ++i) {
            float val = (float) Math.pow(i + 1, 3);
            if (i == 0) val *= 2;
            ShadowCascade shadowCascade = new ShadowCascade(val);
            shadowCascades.add(shadowCascade);
        }
    }

    private void update(Camera camera, Scene scene) {
        for (int i = 0; i < CASCADE_NUM; ++i) {
            ShadowCascade shadowCascade = shadowCascades.get(i);
            shadowCascade.update(camera, scene.light);
        }
    }

    public void render(Camera camera, Scene scene, Transformations transformations, FrustumCullFilter frustumCullFilter) {
        update(camera, scene);

        glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
        glViewport(0, 0, ShadowBuffer.SHADOW_MAP_WIDTH, ShadowBuffer.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShader.bind();

        glDisable(GL_CULL_FACE);

        for (int i = 0; i < CASCADE_NUM; ++i) {
            ShadowCascade shadowCascade = shadowCascades.get(i);

            Matrix4f projectionMatrix = shadowCascade.getOrthoProjectionMatrix();
            Matrix4f lightViewMatrix = shadowCascade.getLightViewMatrix();

            depthShader.setUniform("orthoProjectionMatrix", projectionMatrix);
            depthShader.setUniform("lightViewMatrix", lightViewMatrix);

            frustumCullFilter.updateFrustum(projectionMatrix, lightViewMatrix);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
            glClear(GL_DEPTH_BUFFER_BIT);

            renderChunks(scene, transformations, frustumCullFilter);
        }

        glEnable(GL_CULL_FACE);

        // Unbind
        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindTextures(int start) {
        shadowBuffer.bindTextures(start);
    }

    private void renderChunks(Scene scene, Transformations transformations, FrustumCullFilter frustumCullFilter) {
        bindTextures(GL_TEXTURE2);
        for (Chunk chunk : scene.chunkManager.getChunks()) {
            depthShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
            chunk.renderSolid(frustumCullFilter);
        }
        for (Chunk chunk : scene.chunkManager.getChunks()) {
            depthShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
            chunk.renderTransparencies(frustumCullFilter);
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
