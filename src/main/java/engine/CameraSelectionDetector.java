package engine;

import engine.Camera;
import engine.world.Block;
import engine.world.Chunk;
import engine.world.TextureManager;
import javafx.scene.transform.Transform;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3d;
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

    private Vector3f selectBlock(Chunk[][] chunks, Vector3f center, Vector3f dir) {
        Chunk selectedChunk = null;
        float blockClosestDistance = Float.POSITIVE_INFINITY;
        float chunkClosestDistance = Float.POSITIVE_INFINITY;

        for (Chunk[] chunkList : chunks) {
            for (Chunk chunk : chunkList) {
                min.set(chunk.getPosition());
                max.set(chunk.getPosition());
                max.add(Chunk.getX(), Chunk.getY(), Chunk.getZ()); // the size of chunk
                if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < chunkClosestDistance) {
                    chunkClosestDistance = nearFar.x;
                    selectedChunk = chunk;
                }
            }
        }

        if (selectedChunk != null) {
            Block block;
            Block selectedBlock = null;
            for (int i = 0; i < Chunk.getX(); ++i) {
                for (int j = 0; j < Chunk.getY(); ++j) {
                    for (int k = 0; k < Chunk.getZ(); ++k) {
                        block = selectedChunk.getBlock(i, j, k);
                        if (block == null) {
                            return null;
                        }
                        if (block.getType() != TextureManager.SOLID) continue;
                        min.set(block.getPosition());
                        max.set(block.getPosition());
                        max.add(1, 1, 1);
                        if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < blockClosestDistance) {
                            blockClosestDistance = nearFar.x;
                            selectedBlock = block;
                        }
                    }
                }
            }
            if (selectedBlock == null || blockClosestDistance >= 5) return null;
            else return selectedBlock.getPosition();
        } else {
            return null;
        }

//        for (Chunk[] gameItem : gameItems) {
//            gameItem.setSelected(false);
//            min.set(gameItem.getPosition());
//            max.set(gameItem.getPosition());
//            min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
//            max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
//            if (Intersectionf.intersectRayAab(center, dir, min, max, nearFar) && nearFar.x < closestDistance) {
//                closestDistance = nearFar.x;
//                selectedGameItem = gameItem;
//            }
//        }
//
//        if (selectedGameItem != null) {
//            selectedGameItem.setSelected(true);
//        }
    }

}
