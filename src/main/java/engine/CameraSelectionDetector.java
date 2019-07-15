package engine;

import engine.maths.Transformations;
import engine.world.Block;
import engine.world.Chunk;
import engine.world.TextureManager;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CameraSelectionDetector {

    private final Vector3f max;

    private final Vector3f min;

    private final Vector2f nearFar;

    private Vector3f dir;

    public CameraSelectionDetector() {
        dir = new Vector3f();
        min = new Vector3f();
        max = new Vector3f();
        nearFar = new Vector2f();
    }

    public Vector3f selectBlock(Iterable<Chunk> chunks, Camera camera, Transformations transformations) {
        dir = transformations.getViewMatrix(camera).positiveZ(dir).negate();
        return selectBlock(chunks, camera.getPosition(), dir);
    }

    private float manhattan_distance(float x1, float z1, float x2, float z2) {
        return Math.abs(x1 - x2) + Math.abs(z1 - z2);
    }

    private Vector3f selectBlock(Iterable<Chunk> chunks, Vector3f center, Vector3f dir) {
        Block block;
        Block selectedBlock = null;
        float blockClosestDistance = Float.POSITIVE_INFINITY;

        for (Chunk chunk : chunks) {
            min.set(chunk.getPosition());
            max.set(chunk.getPosition());
            max.add(Chunk.getX(), Chunk.getY(), Chunk.getZ()); // the size of chunk
            if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar)) {
                float dist = ((chunk.getx() << 4) + 8 - center.x) * ((chunk.getx() << 4) + 8 - center.x)
                        + ((chunk.getz() << 4) + 8 - center.z) * ((chunk.getz() << 4) + 8 - center.z);
                if (dist > 266.13708498984755)
                    continue; // (8 * (2 ** .5) + 5) ** 2
                for (int i = 0; i < Chunk.getX(); ++i) {
                    for (int j = (int) Math.max(center.y - 6, 0); j < (int) Math.min(center.y + 6, Chunk.getY()); ++j) {
                        for (int k = 0; k < Chunk.getZ(); ++k) {
                            if (manhattan_distance((chunk.getx() << 4) + i, (chunk.getz() << 4) + k, center.x,
                                    center.z) > 5)
                                continue;
                            block = chunk.getBlock(i, j, k);
                            if (block == null) {
                                return null;
                            }
                            if (block.getBlockID() == TextureManager.AIR)
                                continue;
                            min.set(block.getPosition());
                            max.set(block.getPosition());
                            max.add(1, 1, 1);
                            if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x <= 5
                                    && nearFar.x < blockClosestDistance) {
                                blockClosestDistance = nearFar.x;
                                selectedBlock = block;
                            }
                        }
                    }
                }
            }
        }
        if (selectedBlock == null)
            return null;
        else
            return selectedBlock.getPosition();
    }

    public Vector3f getNormalVector(Vector3f position, Camera camera, Transformations transformations) {
        dir = transformations.getViewMatrix(camera).positiveZ(dir).negate();
        return getNormalVector(position, camera.getPosition(), dir);
    }

    private boolean checkFace(Vector3f center, Vector3f dir, Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        return Intersectionf.intersectRayTriangleFront(center, dir, a, d, b, 0) != -1
                || Intersectionf.intersectRayTriangleFront(center, dir, b, d, c, 0) != -1;
    }

    private Vector3f getNormalVector(Vector3f blockPosition, Vector3f center, Vector3f dir) {
        // iterate over all faces and determine which is the closest.
        if (blockPosition == null)
            return null;
        Vector3f a = new Vector3f(blockPosition.x, blockPosition.y, blockPosition.z),
                b = new Vector3f(blockPosition.x, blockPosition.y, blockPosition.z + 1),
                c = new Vector3f(blockPosition.x, blockPosition.y + 1, blockPosition.z + 1),
                d = new Vector3f(blockPosition.x, blockPosition.y + 1, blockPosition.z),
                e = new Vector3f(blockPosition.x + 1, blockPosition.y, blockPosition.z),
                f = new Vector3f(blockPosition.x + 1, blockPosition.y, blockPosition.z + 1),
                g = new Vector3f(blockPosition.x + 1, blockPosition.y + 1, blockPosition.z + 1),
                h = new Vector3f(blockPosition.x + 1, blockPosition.y + 1, blockPosition.z);
        if (checkFace(center, dir, b, a, d, c))
            return new Vector3f(-1, 0, 0);
        if (checkFace(center, dir, a, e, h, d))
            return new Vector3f(0, 0, -1);
        if (checkFace(center, dir, e, f, g, h))
            return new Vector3f(1, 0, 0);
        if (checkFace(center, dir, f, b, c, g))
            return new Vector3f(0, 0, 1);
        if (checkFace(center, dir, h, g, c, d))
            return new Vector3f(0, 1, 0);
        if (checkFace(center, dir, a, b, f, e))
            return new Vector3f(0, -1, 0);
        return null;
    }
}
