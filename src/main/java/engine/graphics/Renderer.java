package engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import engine.IO.Window;
import engine.Utils;
import engine.maths.Transformations;
import engine.Camera;
import engine.world.Block;
import engine.world.Chunk;
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
    private final Vector3f ambientLight = new Vector3f(.3f, .3f, .3f);

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

        shader.setUniform("specularPower", specularPower);
        shader.setUniform("ambientLight", ambientLight);
    }

    /**
     * This method renders meshes using the shader that has been
     * initialized in the function init();
     *
     * This method also updates uniform matrices that is used for
     * transformations.
     *
     * @param window Renderer handles events like window resize.
     * @param chunk The chunk that you want to render.
     */
    public void render(Window window, Chunk chunk, DirectionalLight directionalLight) {
        // the window's buffer has been cleaned, in MainEngine.update();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shader.bind();

        Vector4f direction = new Vector4f(camera.getDirection(), 0);
        direction.mul(transformations.getViewMatrix(camera));
        directionalLight.setDirection(new Vector3f(direction.x, direction.y, direction.z));
        shader.setUniform("directionalLight", directionalLight);

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

        shader.setUniform("texture_sampler", 0);

        for (Block block : chunk.renderList) {
            shader.setUniform("modelMatrix",
                    transformations.getModelMatrix(block)
            );
            shader.setUniform("material", block.getMesh().getMaterial());
            block.render();
        }

        shader.unbind();

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
