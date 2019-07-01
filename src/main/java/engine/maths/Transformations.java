package engine.maths;

import engine.Camera;
import engine.world.Block;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformations {
    /**
     * to understand these matrices, visit:
     *      https://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
     */
    private Matrix4f projectionMatrix;
    private Matrix4f worldMatrix;
    private Matrix4f viewMatrix;

    public Transformations() {
        projectionMatrix = new Matrix4f().identity();
        worldMatrix = new Matrix4f().identity();
        viewMatrix = new Matrix4f().identity();
    }

    public final Matrix4f getProjectionMatrix(float FOV, float width, float height,
                                              float zNear, float zFar) {
        projectionMatrix.identity();

        // mat.perspective() applies transformation to an existing transformation
        // therefore we apply mat.identity() first here.
        projectionMatrix.perspective(FOV, width / height, zNear, zFar);

        return projectionMatrix;
    }

    public Matrix4f getModelMatrix(Block block) {
        Matrix4f modelMatrix = new Matrix4f().identity();
        modelMatrix.translate(block.getPosition());
        return modelMatrix;
    }

    public Matrix4f getViewMatrix(Camera camera) {
        viewMatrix.identity();
        viewMatrix.lookAt(
                camera.getPosition(),
                camera.getPosition().add(camera.getDirection()),
                new Vector3f(0, 1, 0)
        );
        return viewMatrix;
    }
}
