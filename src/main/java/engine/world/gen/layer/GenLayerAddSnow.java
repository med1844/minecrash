package engine.world.gen.layer;

public class GenLayerAddSnow extends GenLayer {

    public GenLayerAddSnow(long seed,GenLayer father) {
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
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed(x + areaX, y + areaY);

                // 是海洋
                if (parentValueX1Y1 == 0)
                {
                    result[x + y * areaWidth] = 0;
                }
                else
                {
                    int value = this.nextInt(6);

                    if (value == 0)
                    {
                        // 0则取森林
                        value = 4;
                    }
                    else if (value <= 1)
                    {
                        // 1则取高山
                        value = 3;
                    }
                    else
                    {
                        // 其他取平原
                        value = 1;
                    }

                    result[x + y * areaWidth] = value;
                }
            }
        }

        return result;
    }

}
