package io.github.medioqrity.engine.world.gen.layer;

public abstract class GenLayer {
    public GenLayer father;
    long seed;
    long worldSeed;
    long chunkSeed;

    public GenLayer(long seed) {
        this.seed = seed;
        this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
        this.seed += seed;
        this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
        this.seed += seed;
        this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
        this.seed += seed;
    }

    public static GenLayer[] initializeAllBiomeGenerators(long worldSeed) {
        GenLayer genLayer = new GenLayerIsland(1L);
        genLayer = new GenLayerFuzzZoom(2000L, genLayer);
        GenLayerAddIsland genLayerAddIsland = new GenLayerAddIsland(1L, genLayer);
        GenLayerZoom genLayerZoom = new GenLayerZoom(2001L, genLayerAddIsland);
        GenLayerAddIsland genLayerAddIsland1 = new GenLayerAddIsland(2L, genLayerZoom);
        genLayerAddIsland1 = new GenLayerAddIsland(50L, genLayerAddIsland1);
        genLayerAddIsland1 = new GenLayerAddIsland(70L, genLayerAddIsland1);

        GenLayerRemoveTooMuchOcean genLayerRemoveTooMuchOcean = new GenLayerRemoveTooMuchOcean(2L, genLayerAddIsland1);
        GenLayerAddSnow genLayerAddSnow = new GenLayerAddSnow(2L, genLayerRemoveTooMuchOcean);
        GenLayerAddIsland genLayerAddIsland2 = new GenLayerAddIsland(3L, genLayerAddSnow);

        GenLayerEdge genLayerEdge = new GenLayerEdge(2L, genLayerAddIsland2, 1);
        genLayerEdge = new GenLayerEdge(2L, genLayerEdge, 2);
        genLayerEdge = new GenLayerEdge(3L, genLayerEdge, 3);
        GenLayerZoom genLayerZoom1 = new GenLayerZoom(2002L, genLayerEdge);
        genLayerZoom1 = new GenLayerZoom(2003L, genLayerZoom1);
        GenLayerAddIsland genLayerAddIsland3 = new GenLayerAddIsland(4L, genLayerZoom1);

        GenLayerAddMushroomIsland genLayerAddMushroomIsland = new GenLayerAddMushroomIsland(5L, genLayerAddIsland3);//nothing done here
        GenLayerDeepOcean genLayerDeepOcean = new GenLayerDeepOcean(4L, genLayerAddMushroomIsland);
        GenLayer genlayer4 = GenLayerZoom.magnify(1000L, genLayerDeepOcean, 0);


//        GenLayerSmooth genlayersmooth1 = new GenLayerSmooth(1000L, genlayerhills);
//        GenLayerRiverMix genlayerrivermix = new GenLayerRiverMix(100L, genlayersmooth1, genlayersmooth);
//        GenLayer genlayer3 = new GenLayerVoronoiZoom(10L, genlayerrivermix);
//        genlayerrivermix.initWorldGenSeed(worldSeed);
//        genlayer3.initWorldGenSeed(worldSeed);
//        return new GenLayer[] {genlayerrivermix, genlayer3, genlayerrivermix};


        return new GenLayer[]{genLayer};
    }

    public abstract int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight);

    public void initWorldSeed(long worldSeed) {
        this.worldSeed = worldSeed;
        if (this.father != null) {
            this.father.initWorldSeed(worldSeed);
        }
        this.worldSeed *= this.worldSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldSeed += this.seed;
        this.worldSeed *= this.worldSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldSeed += this.seed;
        this.worldSeed *= this.worldSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldSeed += this.seed;
    }

    public void initChunkSeed(long x, long y) {
        this.chunkSeed = this.worldSeed;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += x;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += y;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += x;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += y;
    }

    /**
     * returns a LCG pseudo random number from [0, x). Args: int x
     */
    protected int nextInt(int upperLimit) {
        int i = (int) ((this.chunkSeed >> 24) % (long) upperLimit);

        if (i < 0) {
            i += upperLimit;
        }

        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += this.worldSeed;
        return i;
    }

    public int selectRandom(int[] array) {
        return array[this.nextInt(array.length)];
    }

    public int selectModeOrRandom(int i, int j, int k, int l) {
        return j == k && k == l ? j :
                (i == j && i == k ? i :
                        (i == j && i == l ? i :
                                (i == k && i == l ? i :
                                        (i == j && k != l ? i :
                                                (i == k && j != l ? i :
                                                        (i == l && j != k ? i :
                                                                (j == k && i != l ? j :
                                                                        (j == l && i != k ? j :
                                                                                (k == l && i != j ? k :
                                                                                        this.selectRandom(new int[]{i, j, k, l}))))))))));
    }


}
