package engine.world.gen.layer;

public class GenLayerVoronoiZoom extends GenLayer {
    
    public GenLayerVoronoiZoom(long seed,GenLayer father) {
        // TODO Auto-generated constructor stub
        super(seed);
        this.father=father;
    }
    
    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        areaX = areaX - 2;
        areaY = areaY - 2;
        int parentAreaX = areaX >> 2;
        int parentAreaY = areaY >> 2;
        int parentWidth = (areaWidth >> 2) + 2;
        int parentHeight = (areaHeight >> 2) + 2;
        // parentRes是本层的1/4
        int[] parentRes = this.father.getInts(parentAreaX, parentAreaY, parentWidth, parentHeight);
        int tmpWidth = parentWidth - 1 << 2;
        int tmpHeight = parentHeight - 1 << 2;
        // 临时结果
        int[] tmp = new int[tmpWidth * tmpHeight];

        for (int parentY = 0; parentY < parentHeight - 1; ++parentY)
        {
            // parent当前点的值
            int parentValue   = parentRes[parentY * parentWidth];
            // parent当前点y+1点的值
            int parentValueY1 = parentRes[(parentY + 1) * parentWidth];

            for (int parentX = 0; parentX < parentWidth - 1; ++parentX)
            {
                // 随机取parent的4个点的坐标
                this.initChunkSeed((parentX + parentAreaX) << 2, (parentY + parentAreaY) << 2);
                double vertex1X = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                double vertex1Y = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                this.initChunkSeed((parentX + parentAreaX + 1) << 2, (parentY + parentAreaY) << 2);
                double vertex2X = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                double vertex2Y = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                this.initChunkSeed((parentX + parentAreaX) << 2, (parentY + parentAreaY + 1) << 2);
                double vertex3X = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D;
                double vertex3Y = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                this.initChunkSeed((parentX + parentAreaX + 1) << 2, (parentY + parentAreaY + 1) << 2);
                double vertex4X = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
                double vertex4Y = ((double)this.nextInt(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;

                int parentValueX1   = parentRes[parentX + 1 +  parentY      * parentWidth] % 256;
                int parentValueX1Y1 = parentRes[parentX + 1 + (parentY + 1) * parentWidth] % 256;

                for (int yLow = 0; yLow < 4; ++yLow)
                {
                    int tmpIndex = ((parentY << 2) + yLow) * tmpWidth + (parentX << 2);

                    for (int xLow = 0; xLow < 4; ++xLow)
                    {
                        // 当前点到parent各点的距离的平方
                        double dist1 = ((double)yLow - vertex1Y) * ((double)yLow - vertex1Y) + ((double)xLow - vertex1X) * ((double)xLow - vertex1X);
                        double dist2 = ((double)yLow - vertex2Y) * ((double)yLow - vertex2Y) + ((double)xLow - vertex2X) * ((double)xLow - vertex2X);
                        double dist3 = ((double)yLow - vertex3Y) * ((double)yLow - vertex3Y) + ((double)xLow - vertex3X) * ((double)xLow - vertex3X);
                        double dist4 = ((double)yLow - vertex4Y) * ((double)yLow - vertex4Y) + ((double)xLow - vertex4X) * ((double)xLow - vertex4X);

                        // 取最近点的值
                        if (dist1 < dist2 && dist1 < dist3 && dist1 < dist4)
                        {
                            tmp[tmpIndex++] = parentValue;
                        }
                        else if (dist2 < dist1 && dist2 < dist3 && dist2 < dist4)
                        {
                            tmp[tmpIndex++] = parentValueX1;
                        }
                        else if (dist3 < dist1 && dist3 < dist2 && dist3 < dist4)
                        {
                            tmp[tmpIndex++] = parentValueY1;
                        }
                        else
                        {
                            tmp[tmpIndex++] = parentValueX1Y1;
                        }
                    }
                }

                // parent当前点移动x+1
                parentValue = parentValueX1;
                parentValueY1 = parentValueX1Y1;
            }
        }

        int[] result = new int[areaWidth * areaHeight];

        // tmp和result尺寸可能不同，这里把tmp左上角部分复制到result
        for (int resultY = 0; resultY < areaHeight; ++resultY)
        {
            System.arraycopy(tmp, (resultY + areaY % 2) * tmpWidth + (areaX % 2), result, resultY * areaWidth, areaWidth);
        }

        return result;
    }

}
