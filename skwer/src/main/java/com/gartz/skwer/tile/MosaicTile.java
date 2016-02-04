package com.gartz.skwer.tile;

import com.gartz.skwer.game.Game;
import com.gartz.skwer.mosaic.ErrorMosaicBuilder;
import com.gartz.skwer.mosaic.GridMosaicBuilder;
import com.gartz.skwer.mosaic.Mosaic;
import com.gartz.skwer.mosaic.RosettaMosaicBuilder;

/**
 * Created by gartz on 2/1/16.
 *
 * The game object for mosaic drawing tiles.
 *
 */
public class MosaicTile extends AnimatedTile {

    Mosaic gridMosaic;
    Mosaic rosettaMosaic;
    Mosaic errorMosaic;

    public MosaicTile(Game game, int i, int j, float x, float y) {
        super(game, i, j, x, y);

        gridMosaic = new GridMosaicBuilder().buildMosaic(x, y);
        rosettaMosaic = new RosettaMosaicBuilder().buildMosaic(x, y);
        errorMosaic = new ErrorMosaicBuilder().buildMosaic(x, y);
    }

    @Override
    public void onDraw(float[] mvpMatrix) {
        super.onDraw(mvpMatrix);
        Mosaic mosaic = getCurrentMosaic();
        mosaic.setColor(currentColor, currentDimFactor);
        mosaic.draw(mvpMatrix);
    }

    @Override
    public void setState(int state, int puzzleCountDelta, boolean isUserAction) {
        int oldState = this.state;
        super.setState(state, puzzleCountDelta, isUserAction);

        if (oldState != state && puzzleCountDelta < 0 && isUserAction)
            getCurrentMosaic().flip((left + right) / 2, (top + bottom) / 2);
    }

    protected Mosaic getCurrentMosaic() {
        if (skwerGame.isInPuzzle() && puzzleCount > -3) {
            if (puzzleCount >= 3)
                return rosettaMosaic;
            else if (puzzleCount < 0)
                return errorMosaic;
        }
        return gridMosaic;
    }

}
