package engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import engine.IO.Window;
import engine.Utils;
import engine.maths.Transformations;
import engine.Camera;
import engine.world.Block;
import engine.world.Chunk;

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
//        shader.createUniform("worldMatrix");
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
    public void render(Window window, Chunk chunk) {
        // the window's buffer has been cleaned, in MainEngine.update();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

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

        shader.setUniform("texture_sampler", 0);

        for (Block block : chunk.renderList) {
            shader.setUniform("modelMatrix",
                    transformations.getModelMatrix(block)
            );
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
