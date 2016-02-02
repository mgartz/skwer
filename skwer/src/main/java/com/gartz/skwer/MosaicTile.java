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

    public MosaicTile(Game game, float x, float y) {
        super(game);
        gridMosaic = new GridMosaicBuilder().buildMosaic(x, y);
        rosettaMosaic = new RosettaMosaicBuilder().buildMosaic(x, y);
        errorMosaic = new ErrorMosaicBuilder().buildMosaic(x, y);
    }

    @Override
    public void onDraw(float[] mvpMatrix) {
        getCurrentMosaic().draw(mvpMatrix);
    }

    protected Mosaic getCurrentMosaic() {
        if (drawRosetta)
            return rosettaMosaic;
        else if (drawError)
            return errorMosaic;
        return gridMosaic;
    }

}
