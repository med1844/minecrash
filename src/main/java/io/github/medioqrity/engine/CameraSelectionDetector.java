package io.github.medioqrity.engine;

import java.lang.Math;

import static java.lang.Math.max;
import static java.lang.Math.min;

import org.joml.*;

import io.github.medioqrity.engine.world.Block;
import io.github.medioqrity.engine.world.ChunkUtils.Chunk;
import io.github.medioqrity.engine.world.ChunkUtils.ChunkManager;
import io.github.medioqrity.engine.world.TextureManager;
import io.github.medioqrity.engine.maths.Transformations;

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

    public Vector3f selectBlock(ChunkManager chunkManager, Camera camera, Transformations transformations) {
        dir = transformations.getViewMatrix(camera).positiveZ(dir).negate();
        return selectBlock(chunkManager, camera.getPosition(), dir);
    }

    private float manhattan_distance(float x1, float z1, float x2, float z2) {
        return Math.abs(x1 - x2) + Math.abs(z1 - z2);
    }

    /**
     * Given the camera position, and the direction the camera is looking for, and the chunks
     * return the block that the cross hair is pointing at
     * @param chunkManager contains all chunk data
     * @param center camera position
     * @param dir where camera is looking at
     * @return the selected block, or null if nothing is selected
     */
    private Vector3f selectBlock(ChunkManager chunkManager, Vector3f center, Vector3f dir) {
        Block block;
        Block selectedBlock = null;
        float blockClosestDistance = Float.POSITIVE_INFINITY;
        final float distance = 5.5f;

        for (int x = max((int) (center.x - distance), 0); x <= min((int) (center.x + distance), Chunk.getY()); ++x) {
            for (int y = max((int) (center.y - distance), 0); y <= min((int) (center.y + distance), Chunk.getY()); ++y) {
                for (int z = max((int) (center.z - distance), 0); z <= min((int) (center.z + distance), Chunk.getY()); ++z) {
                    block = chunkManager.getBlock(x, y, z);
                    if (block == null) return null;
                    if (block.getBlockID() == TextureManager.AIR) continue;
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

        if (selectedBlock == null) return null;
        return selectedBlock.getPosition();

    }

    /**
     * This method is almost the same as the method `selectBlock`, but this method runs faster
     * for far blocks
     * @param chunkManager contains all chunk data
     * @param center camera position
     * @param dir where camera is looking at
     * @return the selected block, or null if nothing is selected
     */
    public Vector3f selectFarBlock(ChunkManager chunkManager, Vector3f center, Vector3f dir) {
        Block block;
        Block selectedBlock = null;
        float blockClosestDistance = Float.POSITIVE_INFINITY;
        final float distance = 5.5f;

        for (Chunk chunk : chunkManager.getChunks()) {
            min.set(chunk.getPosition());
            max.set(chunk.getPosition());
            max.add(Chunk.getX(), Chunk.getY(), Chunk.getZ()); // the size of chunk
            if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar)) {
                float dist = ((chunk.getx() << 4) + 8 - center.x) * ((chunk.getx() << 4) + 8 - center.x)
                        + ((chunk.getz() << 4) + 8 - center.z) * ((chunk.getz() << 4) + 8 - center.z);
                if (dist > 266.13708498984755)
                    continue; // (8 * (2 ** .5) + 5) ** 2
                for (int i = 0; i < Chunk.getX(); ++i) {
                    for (int j = (int) max(center.y - 6, 0); j < (int) min(center.y + 6, Chunk.getY()); ++j) {
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
