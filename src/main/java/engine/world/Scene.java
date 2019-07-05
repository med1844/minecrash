package engine.world;

import engine.graphics.DirectionalLight;
import engine.world.World;

public class Scene {
    public World world;
    public DirectionalLight light;

    public Scene(World world, DirectionalLight light) {
        this.world = world;
        this.light = light;
    }

    public void init() {
        world.init();
    }

    public void clear() {
        world.clear();
    }
}
