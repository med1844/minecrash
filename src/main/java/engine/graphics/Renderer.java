package engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static engine.graphics.DirectionalLight.OrthoCoords;

import engine.CameraSelectionDetector;
import engine.IO.Window;
import engine.Utils;
import engine.maths.Transformations;
import engine.Camera;
import engine.world.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * This class mainly handles the process of rendering,
 * including managing components and the order of rendering.
 *
 * It doesn't call any GL render function, since these are
 * encapsulated in corresponding classes.
 *
 * It handles:
 * - scene rendering
 * - light rendering
 * - shadow mapping
 */
public class Renderer {
    private Shader sceneShader, depthShader;
    private ShadowMap shadowMap;
    private final Transformations transformations;
    private CameraSelectionDetector cameraSelectionDetector;
    private float FOV = (float)Math.toRadians(60.0f);
    private float Z_NEAR = 0.1f;
    private float Z_FAR = 1000.0f;
    private final float specularPower = 10f;
    private final Vector3f ambientLight = new Vector3f(.5f, .5f, .5f);
    private final double PI = Math.acos(-1);

    public Renderer() {
        transformations = new Transformations();
    }

    /**
     * This method initializes the shader program, including
     * loading vsh, fsh shader source code and attach them to
     * the final shader program.
     *
     * @throws Exception when vsh, fsh creation failed or the
     *                   linking process of shader program failed.
     */
    public void init() throws Exception {
        shadowMap = new ShadowMap();
        cameraSelectionDetector = new CameraSelectionDetector();

        setupSceneShader();
        setupDepthShader();
    }

    private void setupSceneShader() throws Exception {
        sceneShader = new Shader();
        sceneShader.createVertexShader(Utils.loadResource("/shader/scene.vsh"));
        sceneShader.createFragmentShader(Utils.loadResource("/shader/scene.fsh"));
        sceneShader.link();

        sceneShader.createUniform("texture_sampler");
        sceneShader.createUniform("shadowMap");

        sceneShader.createUniform("selected");
        sceneShader.createUniform("selectedBlock");

        sceneShader.createUniform("modelMatrix");
        sceneShader.createUniform("projectionMatrix");
        sceneShader.createUniform("modelViewMatrix");
        sceneShader.createUniform("orthoProjectionMatrix");
        sceneShader.createUniform("modelLightViewMatrix");

        sceneShader.createUniform("specularPower");
        sceneShader.createUniform("ambientLight");

        sceneShader.createMaterialUniform("material");
        sceneShader.createDirectionalLightUniform("directionalLight");
    }

    private void setupDepthShader() throws Exception {
        depthShader = new Shader();
        depthShader.createVertexShader(Utils.loadResource("/shader/depth.vsh"));
        depthShader.createFragmentShader(Utils.loadResource("/shader/depth.fsh"));
        depthShader.link();

        depthShader.createUniform("orthoProjectionMatrix");
        depthShader.createUniform("modelLightViewMatrix");
    }

    /**
     * This method renders meshes using the shader that has been
     * initialized in the function init();
     *
     * This method also updates uniform matrices that is used for
     * transformations.
     *
     * @param window Renderer handles events like window resize.
     * @param camera the perspective
     * @param scene the scene to render
     * @param timer the time tick provider that controls directionalLight behavior
     */
    public void render(Window window, Camera camera, Scene scene, Timer timer) {
        // the window's buffer has been cleaned, in MainEngine.update();

        renderDayNightCycle(window, scene.light, timer);

        renderShadowMap(scene, camera);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        renderScene(window, camera, scene);
    }

    private void renderScene(Window window, Camera camera, Scene scene) {
        sceneShader.bind();

        Vector3f selectedBlockPos = cameraSelectionDetector.selectBlock(scene.chunkManager.getChunks(), camera, transformations);
        sceneShader.setUniform("selected", selectedBlockPos != null);
        if (selectedBlockPos != null) sceneShader.setUniform("selectedBlock", selectedBlockPos);
        else sceneShader.setUniform("selectedBlock", new Vector3f(0, 0, 0));

        OrthoCoords orthoCoords = scene.light.getOrthoCoords();

        Matrix4f orthoProjectionMatrix = new Matrix4f().identity().ortho(
                orthoCoords.left,
                orthoCoords.right,
                orthoCoords.bottom,
                orthoCoords.top,
                orthoCoords.front,
                orthoCoords.back
        );

        sceneShader.setUniform("orthoProjectionMatrix", orthoProjectionMatrix);

        // update matrices
        sceneShader.setUniform("projectionMatrix",
                transformations.getProjectionMatrix(
                        FOV,
                        window.getWidth(),
                        window.getHeight(),
                        Z_NEAR,
                        Z_FAR
                )
        );

        Vector3f lightDirection = scene.light.getDirection();
        Matrix4f lightViewMatrix = transformations.getLightViewMatrix(
                lightDirection, camera
        );

        // Update view Matrix
        Matrix4f viewMatrix = transformations.getViewMatrix(camera);

        // Update Light Uniforms
        renderLight(viewMatrix, ambientLight, scene.light);

        sceneShader.setUniform("texture_sampler", 0);
        sceneShader.setUniform("shadowMap", 2);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, TextureManager.material.getTexture().getId());

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMap().getId());

        sceneShader.setUniform("material", TextureManager.material);
        for (Chunk[] chunkList : scene.chunkManager.getChunks()) {
            for (Chunk chunk : chunkList) {
                sceneShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
                sceneShader.setUniform("modelViewMatrix",
                        transformations.buildModelViewMatrix(chunk, viewMatrix)
                );
                sceneShader.setUniform("modelLightViewMatrix",
                        transformations.buildModelLightViewMatrix(chunk, lightViewMatrix)
                );
                chunk.renderSolid();
            }
        }
        for (Chunk[] chunkList : scene.chunkManager.getChunks()) {
            for (Chunk chunk : chunkList) {
                sceneShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
                sceneShader.setUniform("modelViewMatrix",
                        transformations.buildModelViewMatrix(chunk, viewMatrix)
                );
                sceneShader.setUniform("modelLightViewMatrix",
                        transformations.buildModelLightViewMatrix(chunk, lightViewMatrix)
                );
                chunk.renderMovable();
            }
        }
        for (Chunk[] chunkList : scene.chunkManager.getChunks()) {
            for (Chunk chunk : chunkList) {
                sceneShader.setUniform("modelMatrix", transformations.getModelMatrix(chunk));
                sceneShader.setUniform("modelViewMatrix",
                        transformations.buildModelViewMatrix(chunk, viewMatrix)
                );
                sceneShader.setUniform("modelLightViewMatrix",
                        transformations.buildModelLightViewMatrix(chunk, lightViewMatrix)
                );
                chunk.renderTransparencies();
            }
        }
        sceneShader.unbind();

    }

    private void renderLight(Matrix4f viewMatrix, Vector3f ambientLight, DirectionalLight directionalLight) {
        sceneShader.setUniform("specularPower", specularPower);
        sceneShader.setUniform("ambientLight", ambientLight);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight cur = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(cur.getDirection(), 0);
        dir.mul(viewMatrix);
        cur.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShader.setUniform("directionalLight", cur);
    }

    private void renderDayNightCycle(Window window, DirectionalLight directionalLight, Timer timer) {
        // this part adjusts the angle and light color according to current time.
        DayNightCycle.setDirectionalLight(timer.getTimeRatio(), directionalLight, window);
//        DayNightCycle.setDirectionalLight(0.4, directionalLight, window);
    }

    private void renderShadowMap(Scene scene, Camera camera) {
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShader.bind();

        Vector3f lightDirection = new Vector3f(scene.light.getDirection());

        Matrix4f lightViewMatrix = transformations.getLightViewMatrix(
                lightDirection, camera
        );

        OrthoCoords orthoCoords = scene.light.getOrthoCoords();
        Matrix4f orthoProjectionMatrix = new Matrix4f().identity().ortho(
                orthoCoords.left,
                orthoCoords.right,
                orthoCoords.bottom,
                orthoCoords.top,
                orthoCoords.front,
                orthoCoords.back
        );
        depthShader.setUniform("orthoProjectionMatrix", orthoProjectionMatrix);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, TextureManager.material.getTexture().getId());

        for (Chunk[] chunkList : scene.chunkManager.getChunks()) {
            for (Chunk chunk : chunkList) {
                depthShader.setUniform("modelLightViewMatrix",
                        transformations.buildModelLightViewMatrix(chunk, lightViewMatrix)
                );
                chunk.renderSolid();
            }
        }
        for (Chunk[] chunkList : scene.chunkManager.getChunks()) {
            for (Chunk chunk : chunkList) {
                depthShader.setUniform("modelLightViewMatrix",
                        transformations.buildModelLightViewMatrix(chunk, lightViewMatrix)
                );
                chunk.renderMovable();
            }
        }
        for (Chunk[] chunkList : scene.chunkManager.getChunks()) {
            for (Chunk chunk : chunkList) {
                depthShader.setUniform("modelLightViewMatrix",
                        transformations.buildModelLightViewMatrix(chunk, lightViewMatrix)
                );
                chunk.renderTransparencies();
            }
        }

        // Unbind
        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
}
