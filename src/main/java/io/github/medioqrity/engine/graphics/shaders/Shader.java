package io.github.medioqrity.engine.graphics.shaders;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL20.*;

import io.github.medioqrity.engine.graphics.DirectionalLight;
import io.github.medioqrity.engine.graphics.Material;

public class Shader {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private final Map<String, Integer> uniformTable;

    public Shader() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniformTable = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not create uniform:" + uniformName);
        }
        uniformTable.put(uniformName, uniformLocation);
    }

    /**
     * creates uniform that is an array
     * @param uniformName the name of the new uniform
     * @param n the size of the array
     * @throws Exception when failed to create such uniform
     */
    public void createUniform(String uniformName, int n) throws Exception {
        for (int i = 0; i < n; ++i) {
            createUniform(uniformName + "[" + i + "]");
        }
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Dump the matrix into a float buffer
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniformTable.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, Matrix4f value, int index) {
        setUniform(uniformName + "[" + index + "]", value);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniformTable.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniformTable.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniformTable.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, boolean value) {
        glUniform1i(uniformTable.get(uniformName), value ? 1 : 0);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniformTable.get(uniformName), value);
    }

    public void setUniform(String uniformName, DirectionalLight directionalLight) {
        setUniform(uniformName + ".colour", directionalLight.getColour());
        setUniform(uniformName + ".direction", directionalLight.getDirection());
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.getAmbient());
        setUniform(uniformName + ".diffuse", material.getDiffuse());
        setUniform(uniformName + ".specular", material.getSpecular());
        setUniform(uniformName + ".hasTexture", material.getHasTexture());
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void setUniform(String uniformName, float value, int index) {
        setUniform(uniformName + "[" + index + "]", value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    /**
     * this method links the shader program and validates it
     * @throws Exception when linking process failed
     */
    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.out.println("Warning validating Shader code: " + glGetShaderInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void clear() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
