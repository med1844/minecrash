package engine;

import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.Renderer;
import engine.IO.Input;
import engine.world.Chunk;
import engine.world.Scene;
import engine.world.TextureManager;
import engine.world.Timer;
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
    private Input input; // this controls Camera
    private Camera camera;
    private Scene scene;
    private Timer timer;

    public MainEngine(int width, int height, String windowTitle, boolean vSync) {
        game = new Thread(this, "minecrash");
        window = new Window(width, height, windowTitle, vSync);
        renderer = new Renderer();
        input = new Input();
        camera = new Camera();
        Chunk[] chunks = new Chunk[3];
        chunks[0] = new Chunk(0, 0);
        chunks[1] = new Chunk(1, 0);
        chunks[2] = new Chunk(2, 2);
        DirectionalLight directionalLight = new DirectionalLight(
                new Vector3f(1, 1, 1),
                new Vector3f(0, 1, 1),
                0.65f
        );
        scene = new Scene(chunks, directionalLight);
        timer = new Timer(3.0);
    }

    public void init() throws Exception {
        // the order shouldn't be changed
        window.init();
        renderer.init();
        input.init(window, camera);
        TextureManager.init();
        scene.init();
        timer.init();
        scene.light.setOrthoCoords(-50, 50, -50, 50, -50, 50);
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
    }

    public void render() {
        window.clear(); // clear up existing data
        renderer.render(window, camera, scene, timer);
        window.swapBuffers();
    }

    public void clear() {
        renderer.clear();
        scene.clear();
    }

}
