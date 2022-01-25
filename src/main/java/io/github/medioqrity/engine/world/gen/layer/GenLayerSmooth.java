package io.github.medioqrity.engine.world.gen.layer;

public class GenLayerSmooth extends GenLayer {

    public GenLayerSmooth(long seed, GenLayer father) {
        super(seed);
        this.father = father;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentAreaX = areaX - 1;
        int parentAreaY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeight);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                // 以x+1 y+1为中心，十字型采样5个点
                int parentValueY1 = parentRes[x + (y + 1) * parentWidth];
                int parentValueX2Y1 = parentRes[x + 2 + (y + 1) * parentWidth];
                int parentValueX1 = parentRes[x + 1 + y * parentWidth];
                int parentValueX1Y2 = parentRes[x + 1 + (y + 2) * parentWidth];
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];

                if (parentValueY1 == parentValueX2Y1
                        && parentValueX1 == parentValueX1Y2) {
                    this.initChunkSeed(x + areaX, y + areaY);

                    // 如果横、竖相对点各自相等则随机取一个
                    if (this.nextInt(2) == 0) {
                        parentValueX1Y1 = parentValueY1;
                    } else {
                        parentValueX1Y1 = parentValueX1;
                    }
                } else {
                    if (parentValueY1 == parentValueX2Y1) {
                        // 如果横相对两点相等则取其值
                        parentValueX1Y1 = parentValueY1;
                    }

                    if (parentValueX1 == parentValueX1Y2) {
                        // 如果竖相对两点相等则取其值
                        parentValueX1Y1 = parentValueX1;
                    }
                } // 其他情况则不变

                result[x + y * areaWidth] = parentValueX1Y1;
            }
        }

        return result;
    }

}
