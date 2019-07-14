package engine.graphics.shadow;

import engine.Camera;
import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.Renderer;
import engine.maths.Transformations;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShadowCascade {

    private static final int FRUSTUM_CORNERS = 8;
    private final Matrix4f projectionViewMatrix;
    private final Matrix4f orthoProjectionMatrix;
    private final Matrix4f lightViewMatrix;

    /**
     * Center of the view cuboid un world space coordinates.
     */
    private final Vector3f centroid;
    private final Vector3f[] frustumCorners;
    private final float zNear;
    private final float zFar;
    private final Vector4f tmpVec;

    public ShadowCascade(float zNear, float zFar) {
        this.zNear = zNear;
        this.zFar = zFar;
        this.projectionViewMatrix = new Matrix4f();
        this.orthoProjectionMatrix = new Matrix4f();
        this.centroid = new Vector3f();
        this.lightViewMatrix = new Matrix4f();
        this.frustumCorners = new Vector3f[FRUSTUM_CORNERS];
        for (int i = 0; i < FRUSTUM_CORNERS; i++) {
            frustumCorners[i] = new Vector3f();
        }
        tmpVec = new Vector4f();
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public Matrix4f getOrthoProjectionMatrix() {
        return orthoProjectionMatrix;
    }

    public void update(Camera camera, DirectionalLight light, int i) {
        lightViewMatrix.identity();
        lightViewMatrix.lookAt(
                camera.getPosition().add(light.getDirection()),
                camera.getPosition(),
                new Vector3f(0, 1, 0)
        );
        ++i;
        float val = i * i * i;
        if (i == 1) val *= 2;
        orthoProjectionMatrix.setOrtho(-20 * val, 20 * val, -20 * val, 20 * val, -20 * val, 20 * val);
    }
}
