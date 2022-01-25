package io.github.medioqrity.engine;

import org.joml.Vector3f;

import io.github.medioqrity.engine.IO.Window;
import io.github.medioqrity.engine.graphics.DirectionalLight;
import io.github.medioqrity.engine.graphics.HUD.Inventory;
import io.github.medioqrity.engine.graphics.Renderer;
import io.github.medioqrity.engine.IO.Input;
import io.github.medioqrity.engine.world.ChunkUtils.ChunkManager;
import io.github.medioqrity.engine.world.Scene;
import io.github.medioqrity.engine.world.TextureManager;
import io.github.medioqrity.engine.world.Timer;

/**
 * This class is the main io.github.medioqrity.engine of the io.github.medioqrity.game, mainly handling:
 * - creation of the io.github.medioqrity.game Thread
 * - how the io.github.medioqrity.game loops
 * - the order of rendering
 * - cleaning up garbage after the io.github.medioqrity.game is closed
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
    private Inventory inventory;

    public MainEngine(int width, int height, String windowTitle, boolean vSync) {
        game = new Thread(this, "minecrash");
        window = new Window(width, height, windowTitle, vSync);
        renderer = new Renderer();
        input = new Input();
        camera = new Camera();
        timer = new Timer(1.0);
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
        inventory = new Inventory();
    }

    @Override
    public void run() {
        try {
            init();
            while (!window.shouldClose()) {
                selectedBlockPos = cameraSelectionDetector.selectBlock(scene.chunkManager, camera, renderer.getTransformations());
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
        input.update(selectedBlockPos, scene, normalVector, inventory); // the input class would update camera.
        timer.update();
        window.update();
    }

    public void render() {
        window.clear(); // clear up existing data
        renderer.render(window, camera, scene, timer, selectedBlockPos, inventory);
        window.swapBuffers();
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
