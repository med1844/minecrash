package engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {

    public static int SHADOW_MAP_WIDTH = 1 << 12;
    public static int SHADOW_MAP_HEIGHT = 1 << 12;
    private int depthMapFBO;
    private Texture depthMap;

    /**
     * this creates an empty shadow map.
     * in order to use depth information, render shadow before render camera
     * @throws Exception when fails creating texture
     */
    public ShadowMap() throws Exception {
        depthMapFBO = glGenFramebuffers();

        // Create the depth map texture
        depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        // Attach the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);
        // Set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public Texture getDepthMap() {
        return depthMap;
    }

    public void clear() {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.clear();
    }

}
