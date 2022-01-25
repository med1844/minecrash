package io.github.medioqrity.engine.graphics.shadow;

import io.github.medioqrity.engine.Camera;
import io.github.medioqrity.engine.graphics.DirectionalLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShadowCascade {

    private final Matrix4f orthoProjectionMatrix;
    private final Matrix4f lightViewMatrix;

    public ShadowCascade(float val) {
        this.orthoProjectionMatrix = new Matrix4f().setOrtho(-20 * val, 20 * val, -20 * val, 20 * val, -20 * val, 20 * val);
        this.lightViewMatrix = new Matrix4f();
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public Matrix4f getOrthoProjectionMatrix() {
        return orthoProjectionMatrix;
    }

    public void update(Camera camera, DirectionalLight light) {
        lightViewMatrix.identity();
        lightViewMatrix.lookAt(
                camera.getPosition().add(light.getDirection()),
                camera.getPosition(),
                new Vector3f(0, 1, 0)
        );
    }
}
