package engine.graphics.shaders;

public class ShaderFactory {

    public static Shader newShader(String arg) throws Exception {
        switch (arg) {
            case "scene":
                return new SceneShader();
            case "depth":
                return new DepthShader();
            case "particle":
                return new ParticleShader();
            case "HUD":
                return new HUDShader();
            default:
                throw new Exception("[ERROR] ShaderFactory.newShader(): Invalid Parameter!");
        }
    }
}
