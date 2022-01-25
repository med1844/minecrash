package io.github.medioqrity.engine.world.gen.layer;

public class GenLayerZoom extends GenLayer {

    public GenLayerZoom(long seed, GenLayer father) {
        super(seed);
        this.father = father;
    }

    public static GenLayer magnify(long i, GenLayer genlayer, int j) {
        Object object = genlayer;

        for (int k = 0; k < j; ++k) {
            object = new GenLayerZoom(i + (long) k, (GenLayer) object);
        }

        return (GenLayer) object;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentAreaX = areaX / 2;
        int parentAreaY = areaY / 2;
        int parentWidth = areaWidth / 2 + 2;
        int parentHeight = areaHeight / 2 + 2;
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeight);
        int tmpWidth = (parentWidth - 1) * 2;
        int tmpHeight = (parentHeight - 1) * 2;

        int[] tmp = new int[tmpWidth * tmpHeight];

        for (int parentY = 0; parentY < parentHeight - 1; ++parentY) {
            int tmpIndex = (parentY * 2) * tmpWidth;
            int parentValue = parentRes[parentY * parentWidth];
            // parentValueY+1
            int parentValueY1 = parentRes[(parentY + 1) * parentWidth];

            for (int parentX = 0; parentX < parentWidth - 1; ++parentX) {
                this.initChunkSeed((parentX + parentAreaX) * 2, (parentY + parentAreaY) * 2);
                int parentValueX1 = parentRes[parentX + 1 + parentY * parentWidth];
                int parentValueX1Y1 = parentRes[parentX + 1 + (parentY + 1) * parentWidth];

                // currentValue = parentValue
                tmp[tmpIndex] = parentValue;
                // currentY+1=  parentValue or parentValueY1
                tmp[tmpIndex + tmpWidth] = this.selectRandom(new int[]{parentValue, parentValueY1});

                ++tmpIndex;

                tmp[tmpIndex] = this.selectRandom(new int[]{parentValue, parentValueX1});
                tmp[tmpIndex + tmpWidth] = this.selectModeOrRandom(parentValue, parentValueX1, parentValueY1, parentValueX1Y1);
                ++tmpIndex;

                parentValue = parentValueX1;
                parentValueY1 = parentValueX1Y1;
            }
        }

        int[] result = new int[areaWidth * areaHeight];//should be replaced with cache later

        // copy the left-top part of tmp to result
        for (int resultY = 0; resultY < areaHeight; ++resultY) {
            System.arraycopy(tmp, (resultY + areaY % 2) * tmpWidth + areaX % 2, result, resultY * areaWidth, areaWidth);
        }

        return result;
    }

}
