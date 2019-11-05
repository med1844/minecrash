package io.github.medioqrity.engine.world.gen.layer;

public class GenLayerRemoveTooMuchOcean extends GenLayer {

    public GenLayerRemoveTooMuchOcean(long seed, GenLayer father) {
        super(seed);
        this.father = father;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentAreaX = areaX - 1;
        int parentAreaY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeihgt = areaHeight + 2;
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeihgt);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                // 以x+1 y+1为中心，十字型采样5个点
                int parentValueX1 = parentRes[x + 1 + y * parentWidth];
                int parentValueX2Y1 = parentRes[x + 2 + (y + 1) * parentWidth];
                int parentValueY1 = parentRes[x + (y + 1) * parentWidth];
                int parentValueX1Y2 = parentRes[x + 1 + (y + 2) * parentWidth];
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];

                // 先取中心的值
                result[x + y * areaWidth] = parentValueX1Y1;
                this.initChunkSeed(x + areaX, y + areaY);
                if (parentValueX1Y1 == 0 && parentValueX1 == 0 && parentValueX2Y1 == 0 && parentValueY1 == 0 && parentValueX1Y2 == 0 && this.nextInt(2) == 0) {
                    // 如果5个点全是0，有1/2概率取1
                    result[x + y * areaWidth] = 1;
                }
            }
        }

        return result;
    }

}
