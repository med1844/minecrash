package engine.world.gen.layer;

public class GenLayerAddIsland extends GenLayer {

    public GenLayerAddIsland(long seed, GenLayer father) {
        // TODO Auto-generated constructor stub
        super(seed);
        this.father = father;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        // TODO Auto-generated method stub
        int parentAreaX = areaX - 1;
        int parentAreaY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeight);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                // 以x+1 y+1为中心，X型采样5个点
                int parentValue = parentRes[x + y * parentWidth];
                int parentValueX2 = parentRes[x + 2 + y * parentWidth];
                int parentValueY2 = parentRes[x + (y + 2) * parentWidth];
                int parentValueX2Y2 = parentRes[x + 2 + (y + 2) * parentWidth];
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed(x + areaX, y + areaY);

                // 中心不为0 或 周围全是0
                if (parentValueX1Y1 != 0 || (parentValue == 0 && parentValueX2 == 0 && parentValueY2 == 0 && parentValueX2Y2 == 0)) {
                    // 中心>0 且 周围出现了0
                    if (parentValueX1Y1 > 0 && (parentValue == 0 || parentValueX2 == 0 || parentValueY2 == 0 || parentValueX2Y2 == 0)) {
                        // 1/5概率变为海洋
                        if (this.nextInt(5) == 0) {
                            // 中心是森林则不变为海洋
                            if (parentValueX1Y1 == 4) {
                                result[x + y * areaWidth] = 4;
                            } else {
                                result[x + y * areaWidth] = 0;
                            }
                        } else {
                            result[x + y * areaWidth] = parentValueX1Y1;
                        }
                    } else {
                        result[x + y * areaWidth] = parentValueX1Y1;
                    }
                } else {
                    // 概率中的分母
                    int deno = 1;
                    int value = 1;

                    // 选择一个不为0的值，越往后重新选的概率越小

                    if (parentValue != 0 && this.nextInt(deno++) == 0) {
                        value = parentValue;
                    }

                    if (parentValueX2 != 0 && this.nextInt(deno++) == 0) {
                        value = parentValueX2;
                    }

                    if (parentValueY2 != 0 && this.nextInt(deno++) == 0) {
                        value = parentValueY2;
                    }

                    if (parentValueX2Y2 != 0 && this.nextInt(deno++) == 0) {
                        value = parentValueX2Y2;
                    }

                    // 1/3的概率设置为刚才选的值
                    if (this.nextInt(3) == 0) {
                        result[x + y * areaWidth] = value;
                    } else if (value == 4) // 森林
                    {
                        result[x + y * areaWidth] = 4;
                    } else {
                        result[x + y * areaWidth] = 0;
                    }
                }
            }
        }

        return result;
    }

}
