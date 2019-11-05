package io.github.medioqrity.engine.world.gen.layer;

public class GenLayerIsland extends GenLayer {

    public GenLayerIsland(long seed, GenLayer father) {
        super(seed);
        this.father = father;
    }

    public GenLayerIsland(long seedBase) {
        super(seedBase);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] result = new int[areaWidth * areaHeight]; // can be faster with cache

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed((long) (areaX + x), (long) (areaY + y));
                // 1/10-sea, 9/10-land
                result[x + y * areaWidth] = this.nextInt(10) == 0 ? 1 : 0;
            }
        }

        if (-areaWidth < areaX && areaX <= 0 && -areaHeight < areaY && areaY <= 0) {
            result[-areaX - areaY * areaWidth] = 1;
        }
        return result;
    }

}
