package engine;

import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.Renderer;
import engine.IO.Input;
import engine.world.TextureManager;
import engine.world.Timer;
import engine.world.World;

import org.joml.Vector3f;

/**
 * This class is the main engine of the game, mainly handling:
 * - creation of the game Thread
 * - how the game loops
 * - the order of rendering
 * - cleaning up garbage after the game is closed
 */
public class MainEngine implements Runnable {
    private Thread game;
    private Window window;
    private Renderer renderer;
    private World world;
    private Input input; // this controls Camera
    private Camera camera;
    private DirectionalLight directionalLight;
    private Timer timer;

    public MainEngine(int width, int height, String windowTitle, boolean vSync) {
        game = new Thread(this, "minecrash");
        window = new Window(width, height, windowTitle, vSync);
        renderer = new Renderer();
        input = new Input();
        camera = new Camera();
        world = new World();
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 5, 0), 0.65f);
        timer = new Timer(10.0);
    }

    public void init() throws Exception {
        window.init();
        renderer.init(camera);
        input.init(window, camera);
        TextureManager.init();
        world.init();
        timer.init();
    }

    @Override
    public void run() {
        try {
            init();
            while (!window.shouldClose()) {
                update();
                render();
            }
            clear();
        } catch (Exception e) {
            System.err.println("[ERROR] MainEngine.run():\r\n" + e);
        }
    }

    public void update() {
        input.update(); // the input class would update camera.
        timer.update();
        window.update();
    }

    public void render() {
        window.clear(); // clear up existing data
        world.render(renderer, window, directionalLight, timer);
        window.swapBuffers();
    }

    public void clear() {
        renderer.clear();
        world.clear();
    }

}
