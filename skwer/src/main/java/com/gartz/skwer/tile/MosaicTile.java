package com.gartz.skwer.tile;

import com.gartz.skwer.Hints;
import com.gartz.skwer.game.Game;
import com.gartz.skwer.mosaic.TransitionMosaic;
import com.gartz.skwer.mosaic.builder.ErrorMosaicBuilder;
import com.gartz.skwer.mosaic.builder.GridMosaicBuilder;
import com.gartz.skwer.mosaic.Mosaic;
import com.gartz.skwer.mosaic.builder.RosettaMosaicBuilder;

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
    Mosaic lastMosaic;
    TransitionMosaic transitionMosaic;
    long previousHintTime;
    long hintFlipTime = Hints.HINT_BACKGROUND_PHASE_1_PERIOD + Hints.HINT_BACKGROUND_PHASE_2_PERIOD/2 - ANIMATION_PERIOD;

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

        if (hintCount > 0) {
            long hintTime = skwerGame.getHintTime();
            if (previousHintTime < hintFlipTime && hintTime >= hintFlipTime)
                lastMosaic.flip((left + right) / 2, (top + bottom) / 2);
            previousHintTime = hintTime;
        }

        if (mosaic == transitionMosaic)
            game.requestRender();
    }

    @Override
    public void setState(int state, int puzzleCountDelta, boolean isUserAction) {
        int oldState = this.state;
        super.setState(state, puzzleCountDelta, isUserAction);

        if (oldState != state && puzzleCountDelta < 0 && isUserAction)
            lastMosaic.flip((left + right) / 2, (top + bottom) / 2);
    }

    protected Mosaic getCurrentMosaic() {
        Mosaic nextMosaic = getMosaicForCurrentState();

        if (transitionMosaic != null) {
            if (transitionMosaic.isTransitionDone()) {
                lastMosaic = transitionMosaic.getTarget();
                transitionMosaic = null;
            }
            else
                return transitionMosaic;
        }

        if (nextMosaic != lastMosaic && lastMosaic != null) {
            transitionMosaic = new TransitionMosaic();
            transitionMosaic.prepareMatches(lastMosaic, nextMosaic);
            transitionMosaic.startTransition();
            return transitionMosaic;
        }

        lastMosaic = nextMosaic;

        return nextMosaic;
    }
    protected Mosaic getMosaicForCurrentState() {
        if (skwerGame.isInPuzzle() && puzzleCount > -3) {
            if (puzzleCount >= 3)
                return rosettaMosaic;
            else if (puzzleCount < 0)
                return errorMosaic;
        }
        return gridMosaic;
    }

}
