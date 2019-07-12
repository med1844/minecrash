package engine.world.gen.layer;

public class GenLayerAddMushroomIsland extends GenLayer{

    public GenLayerAddMushroomIsland(long seed,GenLayer father) {
        // TODO Auto-generated constructor stub
        super(seed);
        this.father=father;
    }
    
    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentAreaX = areaX - 1;
        int parentAreaY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeight);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y)
        {
            for (int x = 0; x < areaWidth; ++x)
            {
                // 以x+1 y+1为中心，X型采样5个点
                int parentValue     = parentRes[x     +  y      * parentWidth];
                int parentValueX2   = parentRes[x + 2 +  y      * parentWidth];
                int parentValueY2   = parentRes[x     + (y + 2) * parentWidth];
                int parentValueX2Y2 = parentRes[x + 2 + (y + 2) * parentWidth];
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed(x + areaX, y + areaY);

                // 如果全是海洋，有1/100概率变成蘑菇岛
                if (parentValueX1Y1 == 0 && parentValue == 0 && parentValueX2 == 0 && parentValueY2 == 0 && parentValueX2Y2 == 0 && this.nextInt(100) == 0)
                {
//                    result[x + y * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
                      result[x + y * areaWidth] = parentValueX1Y1;
                }
                else
                {
                    result[x + y * areaWidth] = parentValueX1Y1;
                }
            }
        }

        return result;
    }

}
