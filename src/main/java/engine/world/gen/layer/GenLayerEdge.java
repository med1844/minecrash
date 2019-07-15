package engine.world.gen.layer;

public class GenLayerEdge extends GenLayer {
    public int genType;

    public GenLayerEdge(long seed, GenLayer father, int genType) {
        // TODO Auto-generated constructor stub
        super(seed);
        this.father = father;
        this.genType = genType;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        // TODO Auto-generated method stub
        switch (genType) {
            case 1:
            default:
                return getIntsCoolWarm(areaX, areaY, areaWidth, areaHeight);
            case 2:
                return getIntsHeatIce(areaX, areaY, areaWidth, areaHeight);
            case 3:
                return getIntsSpecial(areaX, areaY, areaWidth, areaHeight);
        }
    }

    private int[] getIntsCoolWarm(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentAreaX = areaX - 1;
        int parentAreaY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeight);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);

                // 以x+1 y+1为中心，十字型采样5个点
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];
                // 中心是平原
                if (parentValueX1Y1 == 1) {
                    int parentValueX1 = parentRes[x + 1 + y * parentWidth];
                    int parentValueX2Y1 = parentRes[x + 2 + (y + 1) * parentWidth];
                    int parentValueY1 = parentRes[x + (y + 1) * parentWidth];
                    int parentValueX1Y2 = parentRes[x + 1 + (y + 2) * parentWidth];
                    boolean isHill = parentValueX1 == 3 || parentValueX2Y1 == 3 || parentValueY1 == 3 || parentValueX1Y2 == 3;
                    boolean isForest = parentValueX1 == 4 || parentValueX2Y1 == 4 || parentValueY1 == 4 || parentValueX1Y2 == 4;

                    // 周围有高山或森林则取沙漠
                    if (isHill || isForest) {
                        parentValueX1Y1 = 2;
                    }
                }

                result[x + y * areaWidth] = parentValueX1Y1;
            }
        }

        return result;
    }

    private int[] getIntsHeatIce(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentAreaX = areaX - 1;
        int parentAreaY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeight);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                // 以x+1 y+1为中心，十字型采样5个点
                int parentValueX1Y1 = parentRes[x + 1 + (y + 1) * parentWidth];
                // 中心是森林
                if (parentValueX1Y1 == 4) {
                    int parentValueX1 = parentRes[x + 1 + y * parentWidth];
                    int parentValueX2Y1 = parentRes[x + 2 + (y + 1) * parentWidth];
                    int parentValueY1 = parentRes[x + (y + 1) * parentWidth];
                    int parentValueX1Y2 = parentRes[x + 1 + (y + 2) * parentWidth];
                    boolean isDesert = parentValueX1 == 2 || parentValueX2Y1 == 2 || parentValueY1 == 2 || parentValueX1Y2 == 2;
                    boolean isPlain = parentValueX1 == 1 || parentValueX2Y1 == 1 || parentValueY1 == 1 || parentValueX1Y2 == 1;

                    // 周围有平原或沙漠则取高山
                    if (isPlain || isDesert) {
                        parentValueX1Y1 = 3;
                    }
                }

                result[x + y * areaWidth] = parentValueX1Y1;
            }
        }

        return result;
    }

    private int[] getIntsSpecial(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] parentRes = this.father.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] result = new int[areaWidth * areaHeight];

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                int parentValue = parentRes[x + y * areaWidth];

                if (parentValue != 0 && this.nextInt(13) == 0) {
                    // 随机生成1~15，储存在bit8~11，后面GenLayerBiome会用到
                    parentValue |= (1 + this.nextInt(15)) << 8 & 0x0F00;
                }

                result[x + y * areaWidth] = parentValue;
            }
        }

        return result;
    }

}
