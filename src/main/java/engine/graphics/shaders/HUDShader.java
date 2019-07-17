package engine.graphics.shaders;

import engine.Utils;

public class HUDShader extends Shader {

    public HUDShader() throws Exception {
        super();
        createVertexShader(Utils.loadResource("/shader/HUD.vsh"));
        createFragmentShader(Utils.loadResource("/shader/HUD.fsh"));
        link();

        createUniform("projectionMatrix");
        createUniform("modelMatrix");
        createUniform("texture_sampler");
    }
}
