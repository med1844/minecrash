package io.github.medioqrity.engine.graphics;

import org.joml.Vector4f;

public class Material {
    private static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector4f DEFAULT_EMPTY = new Vector4f(0, 0, 0, 1);
    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular;
    private int hasTexture;
    private float reflectance;
    private Texture texture;


    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_EMPTY, 1, reflectance, texture);
    }

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, int hasTexture, float reflectance,
                    Texture texture) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.hasTexture = hasTexture;
        this.reflectance = reflectance;
        this.texture = texture;
    }

    public void clear() {
        texture.clear();
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector4f getAmbient() {
        return ambient;
    }

    public Vector4f getDiffuse() {
        return diffuse;
    }

    public Vector4f getSpecular() {
        return specular;
    }

    public int getHasTexture() {
        return hasTexture;
    }

    public float getReflectance() {
        return reflectance;
    }
}
