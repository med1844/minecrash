package engine.graphics.shaders;

import engine.Utils;

public class ParticleShader extends Shader {

    public ParticleShader() throws Exception {
        super();

        createVertexShader(Utils.loadResource("/shader/particle.vsh"));
        createFragmentShader(Utils.loadResource("/shader/particle.fsh"));
        link();

        createUniform("projectionMatrix");
        createUniform("modelViewMatrix");
        createUniform("texture_sampler");

        createUniform("ambientLight");
        createDirectionalLightUniform("directionalLight");
    }

}
