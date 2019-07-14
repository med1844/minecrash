package engine;

import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.Renderer;
import engine.IO.Input;
import engine.world.ChunkManager;
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
    private Scene scene;
    private Input input; // this controls Camera
    private Camera camera;
    private DirectionalLight directionalLight;
    private Timer timer;
    private CameraSelectionDetector cameraSelectionDetector;
    private Vector3f selectedBlockPos = null;
    private Vector3f normalVector;

    public MainEngine(int width, int height, String windowTitle, boolean vSync) {
        game = new Thread(this, "minecrash");
        window = new Window(width, height, windowTitle, vSync);
        renderer = new Renderer();
        input = new Input();
        camera = new Camera();
        timer = new Timer(10.0);
        cameraSelectionDetector = new CameraSelectionDetector();
        normalVector = null;
    }

    public void init() throws Exception {
        window.init();
        renderer.init();
        input.init(window, camera);
        TextureManager.init();
        ChunkManager chunkManager = new ChunkManager();
        directionalLight = new DirectionalLight(
                new Vector3f(1, 1, 1),
                new Vector3f(0, 5, 0),
                0.65f
        );
        scene = new Scene(
                chunkManager, directionalLight
        );
        scene.init();
        timer.init();
    }

    @Override
    public void run() {
        try {
            init();
            while (!window.shouldClose()) {
                selectedBlockPos = cameraSelectionDetector.selectBlock(scene.chunkManager.getChunks(), camera, renderer.getTransformations());
                if (selectedBlockPos != null) normalVector = cameraSelectionDetector.getNormalVector(selectedBlockPos, camera, renderer.getTransformations());
                update();
                render();
            }
            clear();
        } catch (Exception e) {
            System.err.println("[ERROR] MainEngine.run():\r\n");
            e.printStackTrace();
        }
    }

    public void update() {
        input.update(selectedBlockPos, scene, normalVector); // the input class would update camera.
        timer.update();
        window.update();
    }

    public void render() {
        if (!renderer.isRunning()) {
            window.clear(); // clear up existing data
            renderer.setParameter(window, camera, scene, timer, selectedBlockPos);
            renderer.run();
            window.swapBuffers();
        }
    }

    public void clear() {
        renderer.clear();
        scene.clear();
    }

    public void start() {
        if (game == null) game = new Thread(this, "minecrash_main");
        game.start();
    }

}
