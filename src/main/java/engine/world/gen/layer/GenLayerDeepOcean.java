package engine.world.gen.layer;

public class GenLayerDeepOcean extends GenLayer {


    public GenLayerDeepOcean(long seed, GenLayer father) {
        // TODO Auto-generated constructor stub
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
                int parentValueX1 = parentRes[x + 1 + y * parentWidth];
                int parentValueX2Y1 = parentRes[x + 2 + (y + 1) * parentWidth];
                int parentValueY1 = parentRes[x + (y + 1) * parentWidth];
                int parentValueX1Y2 = parentRes[x + 1 + (y + 2) * parentWidth];
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];

                int oceanCount = 0;

                if (parentValueX1 == 0) {
                    ++oceanCount;
                }

                if (parentValueX2Y1 == 0) {
                    ++oceanCount;
                }

                if (parentValueY1 == 0) {
                    ++oceanCount;
                }

                if (parentValueX1Y2 == 0) {
                    ++oceanCount;
                }

                // 全是海洋则生成深海
                if (parentValueX1Y1 == 0 && oceanCount > 3) {
//                    result[x + y * areaWidth] = BiomeGenBase.deepOcean.biomeID;
                    result[x + y * areaWidth] = parentValueX1Y1;
                } else {
                    result[x + y * areaWidth] = parentValueX1Y1;
                }
            }
        }

        return result;
    }

}
