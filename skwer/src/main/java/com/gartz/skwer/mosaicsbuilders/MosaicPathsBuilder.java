package com.gartz.skwer.mosaicsbuilders;

import java.util.List;
import java.util.Random;

/**
 * Created by Martin on 5/3/2014.
 *
 * Abstract mosaic paths builder.
 *
 */
public abstract class MosaicPathsBuilder {
    protected static final Random random = new Random();

    public abstract List<Mosaic> buildMosaicPaths(int numMosaics, float width, float height, int mosaicDeformation);

    protected float smallTranslation(float size){
        return (-0.5f + random.nextFloat()) * 0.02f * size;
    }
}
