package engine;

import engine.world.Block;
import engine.world.Chunk;
import engine.world.TextureManager;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.maths.Transformations;

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

    public Vector3f selectBlock(Chunk[][] chunks, Camera camera, Transformations transformations) {
        dir = transformations.getViewMatrix(camera).positiveZ(dir).negate();
        return selectBlock(chunks, camera.getPosition(), dir);
    }

    private float manhattan_distance(float x1, float z1, float x2, float z2) {
        return Math.abs(x1 - x2) + Math.abs(z1 - z2);
    }

    private Vector3f selectBlock(Chunk[][] chunks, Vector3f center, Vector3f dir) {
        Block block;
        Block selectedBlock = null;
        float blockClosestDistance = Float.POSITIVE_INFINITY;

        int cnt = 0;
        for (Chunk[] chunkList : chunks) {
            for (Chunk chunk : chunkList) {
                min.set(chunk.getPosition());
                max.set(chunk.getPosition());
                max.add(Chunk.getX(), Chunk.getY(), Chunk.getZ()); // the size of chunk
                if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar)) {
                    float dist = ((chunk.getx() << 4) + 8 - center.x) * ((chunk.getx() << 4) + 8 - center.x) +
                                 ((chunk.getz() << 4) + 8 - center.z) * ((chunk.getz() << 4) + 8 - center.z);
                    if (dist > 266.13708498984755) continue; // (8 * (2 ** .5) + 5) ** 2
                    for (int i = 0; i < Chunk.getX(); ++i) {
                        for (int j = (int) Math.max(center.y - 6, 0); j < (int) Math.min(center.y + 6, Chunk.getY()); ++j) {
                            for (int k = 0; k < Chunk.getZ(); ++k) {
                                if (manhattan_distance((chunk.getx() << 4) + i, (chunk.getz() << 4) + k, center.x, center.z) > 5) continue;
                                block = chunk.getBlock(i, j, k);
                                ++cnt;
                                if (block == null) {
                                    return null;
                                }
                                if (block.getType() != TextureManager.SOLID) continue;
                                min.set(block.getPosition());
                                max.set(block.getPosition());
                                max.add(1, 1, 1);
                                if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x <= 5 && nearFar.x < blockClosestDistance) {
                                    blockClosestDistance = nearFar.x;
                                    selectedBlock = block;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(cnt);
        if (selectedBlock == null) return null;
        else return selectedBlock.getPosition();
    }

}
