package engine.graphics;

import org.joml.Vector3f;

public class Particle {

    private Vector3f speed, position;
    private float scaleX, scaleY;
    private long ttl; // time to live
    private Mesh mesh;

    public Particle(Mesh mesh, Vector3f position, Vector3f speed, long ttl) {
        this.mesh = mesh;
        this.position = position;
        this.speed = speed;
        this.ttl = ttl;
        this.scaleX = (float) Math.random() * 0.15f + 0.1f;
        this.scaleY = (float) Math.random() * 0.15f + 0.1f;
    }

    public Particle(Particle particle) {
        this.mesh = particle.mesh;
        this.speed = new Vector3f(particle.speed);
        this.ttl = particle.ttl;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void update(long elapsedTime) {
        ttl -= elapsedTime;
        float delta = elapsedTime / 250f;
        speed.y -= delta * 1.02f;
        position.x += speed.x * delta * 0.9f;
        position.y += speed.y * delta * 0.9f;
        position.z += speed.z * delta * 0.9f;
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
