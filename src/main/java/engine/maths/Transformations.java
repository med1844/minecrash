package engine.maths;

import engine.Camera;
import engine.graphics.particles.Particle;
import engine.world.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformations {
    /**
     * to understand these matrices, visit:
     * https://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
     */
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Matrix4f lightViewMatrix;
    private Matrix4f modelMatrix;

    public Transformations() {
        projectionMatrix = new Matrix4f().identity();
        viewMatrix = new Matrix4f().identity();
        lightViewMatrix = new Matrix4f().identity();
        modelMatrix = new Matrix4f().identity();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getProjectionMatrix(float FOV, float width, float height,
                                        float zNear, float zFar) {
        projectionMatrix.identity();

        // mat.perspective() applies transformation to an existing transformation
        // therefore we apply mat.identity() first here.
        projectionMatrix.perspective(FOV, width / height, zNear, zFar);

        return projectionMatrix;
    }

    public Matrix4f getModelMatrix(Chunk chunk) {
        modelMatrix.identity();
        modelMatrix.translate(chunk.getPosition());
        return modelMatrix;
    }

    public Matrix4f getModelMatrix(Particle particle) {
        modelMatrix.identity();
        modelMatrix.translate(particle.getPosition()).scaleXY(particle.getScaleX(), particle.getScaleY());
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

    public Matrix4f getLightViewMatrix(Vector3f direction, Camera camera) {
        lightViewMatrix.identity();
        lightViewMatrix.lookAt(
                camera.getPosition().add(direction),
                camera.getPosition(),
                new Vector3f(0, 1, 0)
        );
        return lightViewMatrix;
    }

    public Matrix4f buildModelViewMatrix(Chunk chunk, Matrix4f matrix) {
        return new Matrix4f(matrix).mul(getModelMatrix(chunk));
    }

    public Matrix4f buildModelViewMatrix(Particle particle, Matrix4f matrix) {
        Matrix4f temp = new Matrix4f(matrix).mul(getModelMatrix(particle));
        temp.m00(1).m01(0).m02(0).
                m10(0).m11(1).m12(0).
                m20(0).m21(0).m22(1).scaleXY(particle.getScaleX(), particle.getScaleY());
        return temp;
    }

    public Matrix4f buildModelLightViewMatrix(Chunk chunk, Matrix4f matrix) {
        return new Matrix4f(matrix).mul(getModelMatrix(chunk));
    }
}
