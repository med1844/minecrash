package engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import engine.IO.Window;
import engine.Utils;
import engine.maths.Transformations;
import engine.Camera;
import engine.world.Block;
import engine.world.Chunk;
import engine.world.DayNightCycle;
import engine.world.Timer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * This class is responsible for rendering images to the
 * window.
 */
public class Renderer {
    private Shader shader;
    private Camera camera;
    private final Transformations transformations;
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
     *                   linking process failed.
     */
    public void init(Camera camera) throws Exception {
        this.camera = camera;
        shader = new Shader();
        shader.createVertexShader(Utils.loadResource("/shader/vertex.vsh"));
        shader.createFragmentShader(Utils.loadResource("/shader/fragment.fsh"));
        shader.link();

        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("modelMatrix");
        shader.createUniform("texture_sampler");
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createDirectionalLightUniform("directionalLight");

    }

    /**
     * This method renders meshes using the shader that has been
     * initialized in the function init();
     *
     * This method also updates uniform matrices that is used for
     * transformations.
     *
     * @param window Renderer handles events like window resize.
     * @param chunks Chunks that you want to render.
     */
    public void render(Window window, Chunk[] chunks, DirectionalLight directionalLight,
                       Timer timer) {
        // the window's buffer has been cleaned, in MainEngine.update();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        renderDayNightCycle(window, directionalLight, timer);

        shader.bind();

        // update matrices
        shader.setUniform("projectionMatrix",
                transformations.getProjectionMatrix(
                        FOV,
                        window.getWidth(),
                        window.getHeight(),
                        Z_NEAR,
                        Z_FAR
                )
        );

        shader.setUniform("viewMatrix",
                transformations.getViewMatrix(camera)
        );

        // Update view Matrix
        Matrix4f viewMatrix = transformations.getViewMatrix(camera);

        // Update Light Uniforms
        renderLight(viewMatrix, ambientLight, directionalLight);

        shader.setUniform("texture_sampler", 0);

        for (Chunk chunk : chunks) {
            for (Block block : chunk.renderList) {
                shader.setUniform("modelMatrix",
                        transformations.getModelMatrix(block)
                );
                shader.setUniform("material", block.getMesh().getMaterial());
                block.render();
            }
        }

        shader.unbind();

    }

    public void renderLight(Matrix4f viewMatrix, Vector3f ambientLight, DirectionalLight directionalLight) {
        shader.setUniform("specularPower", specularPower);
        shader.setUniform("ambientLight", ambientLight);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight cur = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(cur.getDirection(), 0);
        dir.mul(viewMatrix);
        cur.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shader.setUniform("directionalLight", cur);
    }

    public void renderDayNightCycle(Window window, DirectionalLight directionalLight, Timer timer) {
        // this part adjusts the angle and light color according to current time.
        DayNightCycle.setDirectionalLight(timer.getTimeRatio(), directionalLight, window);
    }

    /**
     * This method should be called when deleting the Renderer.
     */
    public void clear() {
        if (shader != null) {
            shader.clear();
        }
    }
}
