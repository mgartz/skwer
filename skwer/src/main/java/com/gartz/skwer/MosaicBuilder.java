package com.gartz.skwer;

import java.util.Random;

/**
 * Created by gartz on 2/1/16.
 *
 * A base class for mosaic builders.
 *
 */
public abstract class MosaicBuilder {
    public static final int QUADS_PER_ROW = 5;
    public static final int DEFORMATION = 2;

    Random random = new Random();

    public abstract Mosaic buildMosaic(float offsetX, float offsetY);


    protected float smallTranslation(){
//        return (-0.5f + random.nextFloat()) * 0.02f;
        return 0;
    }
}
