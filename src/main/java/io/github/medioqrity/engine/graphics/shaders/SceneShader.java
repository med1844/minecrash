package io.github.medioqrity.engine.graphics.shaders;

import io.github.medioqrity.engine.Utils;
import io.github.medioqrity.engine.graphics.shadow.ShadowRenderer;

public class SceneShader extends Shader {

    public SceneShader() throws Exception {
        super();
        createVertexShader(Utils.loadResource("/shader/scene.vsh"));
        createFragmentShader(Utils.loadResource("/shader/scene.fsh"));
        link();

        // for block texture
        createUniform("texture_sampler");

        // for block selection
        createUniform("selected");
        createUniform("selectedBlock");

        // for coordination transformation
        createUniform("modelMatrix");
        createUniform("projectionMatrix");
        createUniform("viewMatrix");

        // for cascade shadow maps
        for (int i = 0; i < ShadowRenderer.CASCADE_NUM; ++i) {
            createUniform("shadowMap_" + i);
        }
        createUniform("orthoProjectionMatrix", ShadowRenderer.CASCADE_NUM);
        createUniform("lightViewMatrix", ShadowRenderer.CASCADE_NUM);
        createUniform("cascadeFarPlanes", ShadowRenderer.CASCADE_NUM);

        // for directional light
        createMaterialUniform("material");
        createDirectionalLightUniform("directionalLight");
        createUniform("specularPower");
        createUniform("ambientLight");

        // for fog
        createUniform("fogDensity");
    }

}
