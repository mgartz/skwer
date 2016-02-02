package com.gartz.skwer;

/**
 * Created by gartz on 2/1/16.
 *
 * The game object for mosaic drawing tiles.
 *
 */
public class MosaicTile extends GameObject {

    Mosaic gridMosaic;
    Mosaic rosettaMosaic;
    Mosaic errorMosaic;

    boolean drawRosetta = false;
    boolean drawError = false;

    public MosaicTile(int x, int y) {
        gridMosaic = new GridMosaicBuilder().buildMosaic(x, y);
        rosettaMosaic = new RosettaMosaicBuilder().buildMosaic(x, y);
        errorMosaic = new ErrorMosaicBuilder().buildMosaic(x, y);
    }

    @Override
    public void onDraw(float[] mvpMatrix) {
        if (drawRosetta)
            rosettaMosaic.draw(mvpMatrix);
        else if (drawError)
            errorMosaic.draw(mvpMatrix);
        else
            gridMosaic.draw(mvpMatrix);
    }

}
