package io.github.medioqrity.engine.graphics.shadow;

import io.github.medioqrity.engine.graphics.ArrayTexture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

public class ShadowBuffer {

    public static final int SHADOW_MAP_WIDTH = 1 << 12;
    public static final int SHADOW_MAP_HEIGHT = 1 << 12;
    private final int depthMapFBO;
    private final ArrayTexture depthMap;

    public ShadowBuffer() throws Exception {
        // Create a FBO to render the depth map
        depthMapFBO = glGenFramebuffers();

        // Create the depth map textures
        depthMap = new ArrayTexture(ShadowRenderer.CASCADE_NUM, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        // Attach the the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getIds()[0], 0);

        // Set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public ArrayTexture getDepthMapTexture() {
        return depthMap;
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public void bindTextures(int start) {
        for (int i = 0; i < ShadowRenderer.CASCADE_NUM; i++) {
            glActiveTexture(start + i);
            glBindTexture(GL_TEXTURE_2D, depthMap.getIds()[i]);
        }
    }

    public void clear() {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.clear();
    }

}
