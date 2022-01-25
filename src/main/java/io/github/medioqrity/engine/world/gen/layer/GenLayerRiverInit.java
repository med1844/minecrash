package io.github.medioqrity.engine.world.gen.layer;

public class GenLayerRiverInit extends GenLayer {

    public GenLayerRiverInit(long seed, GenLayer father) {
        super(seed);
        this.father = father;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        // parent是GenLayerDeepOcean
        int[] parentRes = this.father.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                // parent不是海洋则生成2~300000的随机数
                result[x + y * areaWidth] = parentRes[x + y * areaWidth] > 0 ? this.nextInt(299999) + 2 : 0;
            }
        }

        return result;
    }

}
