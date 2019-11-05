package io.github.medioqrity.engine.graphics.shaders;

import io.github.medioqrity.engine.Utils;

public class DepthShader extends Shader {

    public DepthShader() throws Exception {
        super();

        createVertexShader(Utils.loadResource("/shader/depth.vsh"));
        createFragmentShader(Utils.loadResource("/shader/depth.fsh"));
        link();

        createUniform("orthoProjectionMatrix");
        createUniform("lightViewMatrix");
        createUniform("modelMatrix");
    }

}
