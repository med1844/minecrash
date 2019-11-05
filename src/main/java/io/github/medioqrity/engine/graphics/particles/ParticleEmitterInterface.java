package io.github.medioqrity.engine.graphics.particles;

import java.util.List;

import io.github.medioqrity.engine.world.ChunkUtils.ChunkManager;

public interface ParticleEmitterInterface {

    public List<Particle> getParticles();

    public void update(long elapsedTime, ChunkManager chunkManager);

}
