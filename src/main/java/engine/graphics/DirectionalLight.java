package engine.graphics;

import org.joml.Vector3f;

public class DirectionalLight {

    public static class OrthoCoords {
        public float left, right, top, bottom, front, back;
    }

    private Vector3f colour;
    private Vector3f direction;
    private float intensity;
    private OrthoCoords orthoCoords;

    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        set(colour, direction, intensity);
    }

    public DirectionalLight(DirectionalLight dl) {
        set(dl.colour, dl.direction, dl.intensity);
    }

    public void set(Vector3f colour, Vector3f direction, float intensity) {
        this.colour = colour;
        this.direction = direction;
        this.intensity = intensity;
        orthoCoords = new OrthoCoords();
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public OrthoCoords getOrthoCoords() {
        return orthoCoords;
    }

    public void setOrthoCoords(float left, float right, float bottom, float top, float near, float far) {
        orthoCoords.left = left;
        orthoCoords.right = right;
        orthoCoords.bottom = bottom;
        orthoCoords.top = top;
        orthoCoords.front = near;
        orthoCoords.back = far;
    }

}
