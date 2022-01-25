package io.github.medioqrity.engine.world;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;

import io.github.medioqrity.engine.world.ChunkUtils.ChunkManager;
import io.github.medioqrity.engine.graphics.particles.BlockDebrisParticleEmitter;
import io.github.medioqrity.engine.graphics.DirectionalLight;
import io.github.medioqrity.engine.graphics.particles.ParticleEmitterInterface;

import static io.github.medioqrity.engine.world.TextureManager.*;

public class Scene {
    public ChunkManager chunkManager;
    public DirectionalLight light;
    public List<ParticleEmitterInterface> particleEmitters;

    public Scene(ChunkManager chunkManager, DirectionalLight light) {
        this.chunkManager = chunkManager;
        this.light = light;
        particleEmitters = new LinkedList<>();
    }

    public void init() {
        chunkManager.init();
    }

    public void clear() {
        chunkManager.clear();
    }

    public void destroyBlock(Vector3f selectedBlockPos) {
        int x = (int) selectedBlockPos.x, y = (int) selectedBlockPos.y, z = (int) selectedBlockPos.z;
        particleEmitters.add(new BlockDebrisParticleEmitter(x, y, z, chunkManager.getBlock(x, y, z).getBlockID()));
        chunkManager.updateBlock(x, y, z, AIR);
    }

    public void putBlock(Vector3f selectedBlockPos, int blockID) {
        chunkManager.updateBlock((int) selectedBlockPos.x, (int) selectedBlockPos.y, (int) selectedBlockPos.z, blockID);
    }

    public void update(long elapsedTime, Vector3f cameraPosition) {
        Iterator<ParticleEmitterInterface> iter = particleEmitters.iterator();
        while (iter.hasNext()) {
            ParticleEmitterInterface emitter = iter.next();
            emitter.update(elapsedTime, chunkManager);

            // remove empty particle emitters
            if (emitter.getParticles().size() == 0) {
                iter.remove();
            }
        }
        chunkManager.update(cameraPosition);
    }

    public boolean isBlock(Vector3f selectedBlockPos, int blockType) {
        if (selectedBlockPos == null) return false;
        Block block = chunkManager.getBlock((int) selectedBlockPos.x, (int) selectedBlockPos.y, (int) selectedBlockPos.z);
        if (block == null) return false;
        return (block.getType() & blockType) != 0;
    }

    public boolean isBlock(int x, int y, int z, int blockType) {
        Block block = chunkManager.getBlock(x, y, z);
        if (block == null) return false;
        return (block.getType() & blockType) != 0;
    }

}
