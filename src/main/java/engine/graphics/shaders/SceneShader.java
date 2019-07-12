package engine.graphics.shaders;

import engine.Utils;

public class SceneShader extends Shader {

    public SceneShader() throws Exception {
        super();
        createVertexShader(Utils.loadResource("/shader/scene.vsh"));
        createFragmentShader(Utils.loadResource("/shader/scene.fsh"));
        link();

        createUniform("texture_sampler");
        createUniform("shadowMap");

        createUniform("selected");
        createUniform("selectedBlock");

        createUniform("modelMatrix");
        createUniform("projectionMatrix");
        createUniform("modelViewMatrix");
        createUniform("orthoProjectionMatrix");
        createUniform("modelLightViewMatrix");

        createUniform("specularPower");
        createUniform("ambientLight");

        createUniform("fogDensity");

        createMaterialUniform("material");
        createDirectionalLightUniform("directionalLight");
    }

}
