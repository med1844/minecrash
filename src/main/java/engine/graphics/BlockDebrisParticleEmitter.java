package engine.graphics;

import engine.world.TextureManager;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BlockDebrisParticleEmitter implements ParticleEmitterInterface {

    private List<Particle> particles;
    private final float RANDOM_FACTOR = 0.5f;

    public BlockDebrisParticleEmitter(int x, int y, int z, int blockID) {
        particles = new LinkedList<>();
        for (int i = 0; i < 4; ++i ) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    Particle particle = new Particle(TextureManager.meshes[blockID],
                            new Vector3f(x + i / 4.0f + 0.125f, y + j / 4.0f + 0.125f, z + k / 4.0f + 0.125f),
                            new Vector3f(x + i / 4.0f + 0.125f, y + j / 4.0f + 0.125f, z + k / 4.0f + 0.125f).
                                    sub(x + 0.5f, y + 0.5f, z + 0.5f).
                                    add((float) (Math.random() * 2 - 1) * RANDOM_FACTOR, (float) (Math.random() * 2 - 1) * RANDOM_FACTOR, (float) (Math.random() * 2 - 1) * RANDOM_FACTOR),
                            (long) (Math.random() * 2000)
                    );
                    particles.add(particle);
                    System.out.println(particle.getPosition());
                }
            }
        }
    }

    @Override
    public void update(long elapsedTime) {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle particle = iter.next();
            particle.update(elapsedTime);

            // remove dead particles
            if (particle.getTTL() <= 0) {
                iter.remove();
            }
        }
    }

    @Override
    public List<Particle> getParticles() {
        return particles;
    }
}
