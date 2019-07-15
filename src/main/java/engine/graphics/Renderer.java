package engine.graphics;

import engine.Camera;
import engine.IO.Window;
import engine.graphics.particles.Particle;
import engine.graphics.particles.ParticleEmitterInterface;
import engine.graphics.shaders.ParticleShader;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.ShaderFactory;
import engine.graphics.shadow.ShadowCascade;
import engine.graphics.shadow.ShadowRenderer;
import engine.maths.FrustumCullFilter;
import engine.maths.Transformations;
import engine.world.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * This class mainly handles the process of rendering, including managing
 * components and the order of rendering.
 * <p>
 * It doesn't call any GL render function, since these are encapsulated in
 * corresponding classes.
 * <p>
 * It handles: - scene rendering - light rendering - shadow mapping
 */
public class Renderer implements Runnable {
    private Shader sceneShader, depthShader, particleShader;
    private ShadowRenderer shadowRenderer;
    private Fog fog;
    private FrustumCullFilter frustumCullFilter;
    private final Transformations transformations;
    private final float specularPower = 10f;
    private final Vector3f ambientLight = new Vector3f(.5f, .5f, .5f);
    private Window window;
    private Camera camera;
    private Scene scene;
    private Timer timer;
    private Vector3f selectedBlockPos;
    private boolean running = false;
    public static float FOV = (float) Math.toRadians(60.0f);
    public static float Z_NEAR = 0.1f;
    public static float Z_FAR = 1000.0f;

    public Renderer() {
        transformations = new Transformations();
        frustumCullFilter = new FrustumCullFilter();
    }

    /**
     * This method initializes the shader program, including loading vsh, fsh shader
     * source code and attach them to the final shader program.
     *
     * @throws Exception when vsh, fsh creation failed or the linking process of
     *                   shader program failed.
     */
    public void init() throws Exception {
        fog = new Fog();

        sceneShader = ShaderFactory.newShader("scene");
        depthShader = ShaderFactory.newShader("depth");
        particleShader = ShaderFactory.newShader("particle");

        shadowRenderer = new ShadowRenderer(depthShader);
    }

    public void setParameter(Window window, Camera camera, Scene scene, Timer timer, Vector3f selectedBlockPos) {
        this.window = window;
        this.camera = camera;
        this.scene = scene;
        this.timer = timer;
        this.selectedBlockPos = selectedBlockPos;
    }

    /**
     * This method renders meshes using the shader that has been initialized in the
     * function init();
     * <p>
     * This method also updates uniform matrices that is used for transformations.
     */
    public void render() {
        // the window's buffer has been cleaned, in MainEngine.update();

        renderDayNightCycle(window, scene.light, timer);

        // since rendering cascade shadow map is much too complex to put in only one class,
        // it has been encapsulated into a brand new class.
        renderShadowMap(scene);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        renderScene(window, camera, scene, selectedBlockPos);

        renderParticles(window, camera, scene);

        renderCrossHair(window);
    }

    private void renderScene(Window window, Camera camera, Scene scene, Vector3f selectedBlockPos) {
        sceneShader.bind();

        sceneShader.setUniform("selected", selectedBlockPos != null);
        if (selectedBlockPos != null) sceneShader.setUniform("selectedBlock", selectedBlockPos);
        else sceneShader.setUniform("selectedBlock", new Vector3f(0, 0, 0));

        // Update view Matrix & projection Matrix
        Matrix4f viewMatrix = transformations.getViewMatrix(camera);
        Matrix4f projectionMatrix = transformations.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);

        // Update frustum culling
        frustumCullFilter.updateFrustum(projectionMatrix, viewMatrix);

        // Update Light Uniforms
        renderLight(viewMatrix, ambientLight, scene.light, sceneShader);

        // Update matrices
        sceneShader.setUniform("projectionMatrix", projectionMatrix);
        sceneShader.setUniform("viewMatrix", viewMatrix);

        // Update cascade shadows
        List<ShadowCascade> shadowCascades = shadowRenderer.getShadowCascades();
        for (int i = 0; i < ShadowRenderer.CASCADE_NUM; ++i) {
            ShadowCascade shadowCascade = shadowCascades.get(i);
            sceneShader.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjectionMatrix(), i);
            sceneShader.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
            sceneShader.setUniform("cascadeFarPlanes", ShadowRenderer.CASCADE_SPLITS[i], i);
            sceneShader.setUniform("shadowMap_" + i, 2 + i);
        }

        sceneShader.setUniform("texture_sampler", 0);

        sceneShader.setUniform("fogDensity", fog.getDensity());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, TextureManager.material.getTexture().getId());

        shadowRenderer.bindTextures(GL_TEXTURE2);
        sceneShader.setUniform("material", TextureManager.material);
        for (Chunk chunk : scene.chunkManager.getChunks()) {
            sceneShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
            chunk.renderSolid(frustumCullFilter);
        }
        for (Chunk chunk : scene.chunkManager.getChunks()) {
            sceneShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
            chunk.renderTransparencies(frustumCullFilter);
        }
        sceneShader.unbind();

    }

    private void renderLight(Matrix4f viewMatrix, Vector3f ambientLight, DirectionalLight directionalLight, Shader shader) {
        if (!(shader instanceof ParticleShader)) shader.setUniform("specularPower", specularPower);
        shader.setUniform("ambientLight", ambientLight);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight cur = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(cur.getDirection(), 0);
        dir.mul(viewMatrix);
        cur.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shader.setUniform("directionalLight", cur);
    }

    private void renderDayNightCycle(Window window, DirectionalLight directionalLight, Timer timer) {
        // this part adjusts the angle and light color according to current time.
        DayNightCycle.setDirectionalLight(timer.getTimeRatio(), directionalLight, window);
        DayNightCycle.setFog(timer.getTimeRatio(), fog);
    }

    private void renderShadowMap(Scene scene) {
        shadowRenderer.render(camera, scene, transformations, frustumCullFilter);
    }

    private void renderCrossHair(Window window) {
        glPushMatrix();
        glLoadIdentity();

        float vertical = 0.03f;
        float horizontal = vertical * (float) (window.getHeight()) / window.getWidth();
        glLineWidth(2);

        glBegin(GL_LINES);

        glColor3f(1.0f, 1.0f, 1.0f);

        // Horizontal line
        glVertex3f(-horizontal, 0.0f, 0.0f);
        glVertex3f(+horizontal, 0.0f, 0.0f);
        glEnd();

        // Vertical line
        glBegin(GL_LINES);
        glVertex3f(0.0f, -vertical, 0.0f);
        glVertex3f(0.0f, +vertical, 0.0f);
        glEnd();

        glPopMatrix();
    }

    private void renderParticles(Window window, Camera camera, Scene scene) {
        particleShader.bind();

        glDepthMask(false);

        particleShader.setUniform("texture_sampler", 0);

        particleShader.setUniform("projectionMatrix",
                transformations.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR));

        Matrix4f viewMatrix = transformations.getViewMatrix(camera);
        List<ParticleEmitterInterface> emitters = scene.particleEmitters;

        renderLight(viewMatrix, ambientLight, scene.light, particleShader);

        if (emitters != null) {
            for (ParticleEmitterInterface emitter : emitters) {
                for (Particle particle : emitter.getParticles()) {
                    Matrix4f modelViewMatrix = transformations.buildModelViewMatrix(particle, viewMatrix);
                    particleShader.setUniform("modelViewMatrix", modelViewMatrix);
                    particle.render();
                }
            }
        }

        glDepthMask(true);

        particleShader.unbind();
    }

    /**
     * This method should be called when deleting the Renderer.
     */
    public void clear() {
        if (sceneShader != null) {
            sceneShader.clear();
        }
        if (depthShader != null) {
            depthShader.clear();
        }
    }

    public Transformations getTransformations() {
        return transformations;
    }

    @Override
    public void run() {
        running = true;
        render();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

}
