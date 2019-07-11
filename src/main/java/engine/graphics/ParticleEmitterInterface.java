package engine.graphics;

import java.util.List;

public interface ParticleEmitterInterface {

    public List<Particle> getParticles();

    public void update(long elapsedTime);

}
