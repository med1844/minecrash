package io.github.medioqrity.engine.world.gen.layer;

public class GenLayerFuzzZoom extends GenLayerZoom {

    public GenLayerFuzzZoom(long seed, GenLayer father) {
        super(seed, father);
    }

    public int selectModeOrRandom(int i, int j, int k, int l) {
        return selectRandom(new int[]{i, j, k, l});
    }
}
