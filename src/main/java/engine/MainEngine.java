package engine;

import engine.IO.Window;
import engine.graphics.DirectionalLight;
import engine.graphics.Renderer;
import engine.IO.Input;
import engine.world.Chunk;
import engine.world.ChunkProvider;
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
    private Chunk[] chunks;
    
    private Input input; // this controls Camera
    private Camera camera;
    private DirectionalLight directionalLight;
    private Timer timer;
    private int WW;//World Width
    private int WL;//World Length

    public MainEngine(int width, int height, String windowTitle, boolean vSync) {
        game = new Thread(this, "minecrash");
        window = new Window(width, height, windowTitle, vSync);
        renderer = new Renderer();
        input = new Input();
        camera = new Camera();
//        chunks = new Chunk[3];
//        chunks[0] = new Chunk(0, 0);
//        chunks[1] = new Chunk(1, 0);
//        chunks[2] = new Chunk(2, 2);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 5, 0), 0.65f);
        timer = new Timer(10.0);
        WW=2;
        WL=2;
        chunks=new Chunk[WW*WL];
    }

    public void init() throws Exception {
        window.init();
        renderer.init(camera);
        input.init(window, camera);
        TextureManager.init();

//        for (Chunk chunk : chunks) {
//            chunk.init();
//            chunk.genBlockList();
//        }
        for (int i=0;i<chunks.length;i++) {
            chunks[i]=new Chunk(i/WW, i%WW);
            new ChunkProvider().provideChunk(chunks[i]);
            chunks[i].genBlockList();
        }

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
        for (Chunk chunk : chunks) {
            renderer.render(window, chunk, directionalLight, timer);
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
