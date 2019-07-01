package engine;

import engine.IO.Window;
import engine.graphics.Renderer;
import engine.IO.Input;
import engine.world.Chunk;
import engine.world.TextureManager;

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
    private Chunk[] chunks;
    private Input input; // this controls Camera
    private Camera camera;

    public MainEngine(int width, int height, String windowTitle, boolean vSync) {
        game = new Thread(this, "MINECRASH");
        window = new Window(width, height, windowTitle, vSync);
        renderer = new Renderer();
        input = new Input();
        camera = new Camera();
        chunks = new Chunk[3];
        chunks[0] = new Chunk(0, 0);
        chunks[1] = new Chunk(1, -1);
        chunks[2] = new Chunk(2, 2);
    }

    public void init() throws Exception {
        window.init();
        renderer.init(camera);
        input.init(window, camera);
        TextureManager.init();

        for (Chunk chunk : chunks) {
            chunk.init();
            chunk.genBlockList();
        }
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
    }

    public void render() {
        window.clear(); // clear up existing data
        for (Chunk chunk : chunks) {
            renderer.render(window, chunk);
        }
        window.swapBuffers();
    }

    public void clear() {
        renderer.clear();
        for (Chunk chunk : chunks) {
            chunk.clear();
        }
    }

}
