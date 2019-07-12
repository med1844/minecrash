package engine.graphics.particles;

import engine.world.ChunkManager;

import java.util.List;

public interface ParticleEmitterInterface {

    public List<Particle> getParticles();

    public void update(long elapsedTime, ChunkManager chunkManager);

}
