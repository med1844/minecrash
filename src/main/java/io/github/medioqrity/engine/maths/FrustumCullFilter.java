package io.github.medioqrity.engine.maths;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import io.github.medioqrity.engine.world.ChunkUtils.Chunk;

public class FrustumCullFilter {

    private final Matrix4f projectionViewMatrix;
    private FrustumIntersection frustumIntersection;

    public FrustumCullFilter() {
        projectionViewMatrix = new Matrix4f();
        frustumIntersection = new FrustumIntersection();
    }

    public void updateFrustum(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        projectionViewMatrix.set(projectionMatrix);
        projectionViewMatrix.mul(viewMatrix);
        frustumIntersection.set(projectionViewMatrix);
    }

    public boolean insideFrustum(float x, float y, float z, float boundingRadius) {
        return frustumIntersection.testSphere(x, y, z, boundingRadius);
    }

    public boolean insideFrustum(Chunk chunk) {
        Vector3f position = chunk.getPosition();
        return frustumIntersection.testSphere(
                position.x + (Chunk.getX() << 1),
                position.y + 8,
                position.z + (Chunk.getZ() << 1),
                13.856406460551018f // 8 * sqrt(3)
        );
//        return frustumIntersection.testSphere(x0, y0, z0, boundingRadius) ||
//               frustumIntersection.testSphere(x0, y0, z1, boundingRadius) ||
//               frustumIntersection.testSphere(x0, y1, z0, boundingRadius) ||
//               frustumIntersection.testSphere(x0, y1, z1, boundingRadius) ||
//               frustumIntersection.testSphere(x1, y0, z0, boundingRadius) ||
//               frustumIntersection.testSphere(x1, y0, z1, boundingRadius) ||
//               frustumIntersection.testSphere(x1, y1, z0, boundingRadius) ||
//               frustumIntersection.testSphere(x1, y1, z1, boundingRadius);
    }
}
