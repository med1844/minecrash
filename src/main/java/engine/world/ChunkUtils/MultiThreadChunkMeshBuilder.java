package engine.world.ChunkUtils;

import engine.world.Block;
import engine.world.TextureManager;
import javafx.util.Pair;

import java.util.List;

public class MultiThreadChunkMeshBuilder implements Runnable {

    private Thread thread;
    private Chunk chunk;
    private ChunkManager chunkManager;
    private float[][] positionS;
    private float[][] textureCoordS;
    private float[][] normalS;
    private float[][] adjacentFaceCountS;
    private int[][] indicesS;
    private float[][] positionT;
    private float[][] textureCoordT;
    private float[][] normalT;
    private float[][] adjacentFaceCountT;
    private int[][] indicesT;

    public MultiThreadChunkMeshBuilder(Chunk chunk, ChunkManager chunkManager) {
        this.chunk = chunk;
        this.chunkManager = chunkManager;
        this.thread = new Thread(this);
        positionS = new float[Chunk.getY() >> 4][];
        textureCoordS = new float[Chunk.getY() >> 4][];
        normalS = new float[Chunk.getY() >> 4][];
        adjacentFaceCountS = new float[Chunk.getY() >> 4][];
        indicesS = new int[Chunk.getY() >> 4][];
        positionT = new float[Chunk.getY() >> 4][];
        textureCoordT = new float[Chunk.getY() >> 4][];
        normalT = new float[Chunk.getY() >> 4][];
        adjacentFaceCountT = new float[Chunk.getY() >> 4][];
        indicesT = new int[Chunk.getY() >> 4][];
    }

    @Override
    public void run() {
        long time = System.nanoTime();
        for (int i = 0; i < Chunk.getY() >> 4; ++i) {
            List<Pair<Block, Integer>> l = chunk.generatePartMeshL(chunkManager, i, TextureManager.SOLID);
            positionS[i] = new float[18 * l.size()];
            textureCoordS[i] = new float[12 * l.size()];
            normalS[i] = new float[18 * l.size()];
            adjacentFaceCountS[i] = new float[6 * l.size()];
            indicesS[i] = new int[6 * l.size()];
            for (int j = 0; j < indicesS[i].length; ++j) indicesS[i][j] = j;
            for (Pair<Block, Integer> p : l) {
                chunk.addFace(p.getKey(), p.getValue(), positionS[i], textureCoordS[i], normalS[i], adjacentFaceCountS[i]);
            }
            l = chunk.generatePartMeshL(chunkManager, i, TextureManager.TRANSPARENT);
            positionT[i] = new float[18 * l.size()];
            textureCoordT[i] = new float[12 * l.size()];
            normalT[i] = new float[18 * l.size()];
            adjacentFaceCountT[i] = new float[6 * l.size()];
            indicesT[i] = new int[6 * l.size()];
            for (int j = 0; j < indicesT[i].length; ++j) indicesT[i][j] = j;
            for (Pair<Block, Integer> p : l) {
                chunk.addFace(p.getKey(), p.getValue(), positionT[i], textureCoordT[i], normalT[i], adjacentFaceCountT[i]);
            }
        }
        System.out.println("Mesh data generation time: " + (System.nanoTime() - time));
    }

    public void start() {
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    public void buildMesh() {
        long time = System.nanoTime();
        for (int i = 0; i < Chunk.getY() >> 4; ++i) {
            chunk.buildSolidMesh(i, positionS[i], textureCoordS[i], normalS[i], adjacentFaceCountS[i], indicesS[i], positionS[i].length != 0);
            chunk.buildTransparentMesh(i, positionT[i], textureCoordT[i], normalT[i], adjacentFaceCountT[i], indicesT[i], positionT[i].length != 0);
        }
        System.out.println("Mesh creation time: " + (System.nanoTime() - time));
    }
}
