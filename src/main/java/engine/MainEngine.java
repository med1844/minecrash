package engine;

import engine.IO.Window;
import engine.graphics.Renderer;
import engine.graphics.Mesh;
import engine.graphics.Texture;
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
    private TextureManager textureManager;

    public MainEngine(int width, int height, String windowTitle, boolean vSync) {
        game = new Thread(this, "MINECRASH");
        window = new Window(width, height, windowTitle, vSync);
        renderer = new Renderer();
        input = new Input();
        camera = new Camera();
        chunks = new Chunk[3];
        chunks[0] = new Chunk(0, 0);
        chunks[1] = new Chunk(1, 0);
        chunks[2] = new Chunk(2, 2);
        textureManager = new TextureManager();
    }

    public void init() throws Exception {
        window.init();
        renderer.init(camera);
        input.init(window, camera);
        textureManager.init();

        float[] positions = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,

                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,

                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };
        float[] textureCoord = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                0.0f, 0.0f,
                0.0f, 0.5f,

                0.5f, 0.0f,
                0.5f, 0.5f,

                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,};
        Texture texture = new Texture("/texture/grassblock.png");
        Mesh mesh = new Mesh(positions, textureCoord, indices, texture);

        for (Chunk chunk : chunks) {
            chunk.init(mesh);
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
