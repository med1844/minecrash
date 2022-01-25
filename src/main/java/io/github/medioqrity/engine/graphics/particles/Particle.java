package io.github.medioqrity.engine.graphics.particles;

import org.joml.Vector3f;

import io.github.medioqrity.engine.graphics.Mesh;
import io.github.medioqrity.engine.world.Block;
import io.github.medioqrity.engine.world.ChunkUtils.ChunkManager;
import io.github.medioqrity.engine.world.TextureManager;

public class Particle {

    private Vector3f speed, position;
    private float scaleX, scaleY;
    private long ttl; // time to live
    private Mesh mesh;
    private boolean stop;

    public Particle(Mesh mesh, Vector3f position, Vector3f speed, long ttl) {
        this.mesh = mesh;
        this.position = position;
        this.speed = speed;
        this.ttl = ttl;
        this.scaleX = (float) Math.random() * 0.15f + 0.1f;
        this.scaleY = (float) Math.random() * 0.15f + 0.1f;
        this.stop = false;
    }

    public Particle(Particle particle) {
        this.mesh = particle.mesh;
        this.speed = new Vector3f(particle.speed);
        this.ttl = particle.ttl;
        this.stop = particle.stop;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void update(long elapsedTime, ChunkManager chunkManager) {
        ttl -= elapsedTime;
        if (stop) return;
        float delta = elapsedTime / 250f;
        speed.y -= delta * 1.02f;
        float newX = position.x + speed.x * delta * 0.9f;
        float newY = position.y + speed.y * delta * 0.9f;
        float newZ = position.z + speed.z * delta * 0.9f;
        Block block = chunkManager.getBlock((int) newX, (int) newY, (int) newZ);
        if (block != null && block.getType() == TextureManager.SOLID) {
            speed.x = speed.y = speed.z = 0;
            if (position.y > block.y + 1) position.y = block.y + 1;
            stop = true;
        } else {
            position.x = newX;
            position.y = newY;
            position.z = newZ;
        }
    }

    public void render() {
        mesh.render();
    }

    public long getTTL() {
        return this.ttl;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }
}
