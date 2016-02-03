package com.gartz.skwer.tile;

import com.gartz.skwer.game.Game;
import com.gartz.skwer.SkwerGame;
import com.gartz.skwer.helper.ColorHelper;

/**
 * Created by gartz on 2/2/16.
 *
 * An extension of tile that wraps the animation logic.
 *
 */
public class AnimatedTile extends Tile {
    protected int currentColor = 0xFFFFFFFF;
    private int targetColor;
    private int origColor;
    protected boolean isAnimating;
    private long animationStartTime;
    private long animationPeriod;
    protected float currentDimFactor;
    private float origDimFactor;
    private float targetDimFactor;

    public AnimatedTile(Game game, int x, int y, float x0, float y0) {
        super(game, x, y, x0, y0);
    }

    @Override
    protected void updateToCurrentState(boolean animated) {
        targetColor = COLORS[state];
        if (skwerGame.isInPuzzle()) {
            if (active)
                targetDimFactor = puzzleCount == 0 ? 0.25f : 0;
            else
                targetDimFactor = puzzleCount == 0 ? 0.75f : 0.5f;
        }
        else
            targetDimFactor = 0.75f * (Math.abs(x - SkwerGame.NUM_TILES_X / 2f + 0.5f) / SkwerGame.NUM_TILES_X + Math.abs(y - SkwerGame.NUM_TILES_Y / 2f + 0.5f) / SkwerGame.NUM_TILES_Y);
        if (animated) {
            origColor = currentColor;
            origDimFactor = currentDimFactor;
            isAnimating = true;
            animationStartTime = System.currentTimeMillis();
            animationPeriod = 300;
        }
        game.requestRender();
    }

    @Override
    public void addHintCount() {
        super.addHintCount();
        game.requestRender();
    }

    @Override
    public void onDraw(float[] mvpMatrix) {
        if (isAnimating){
            float t = 1f*(System.currentTimeMillis() - animationStartTime)/animationPeriod;
            if (t >= 1) {
                isAnimating = false;
                t = 1;
            }
            currentColor = ColorHelper.interp(origColor, targetColor, t);
            currentDimFactor = origDimFactor * (1-t) + targetDimFactor * t;
            game.requestRender();
        }
        //TODO
//        if (hintCount > 0){
//            setBackgroundColor(Hints.getHintBackgroundColor());
//            game.requestRender();
//        }
//        else
//            setBackgroundColor(0x00000000);

        if (!isAnimating && hintCount == 0 && !isSelected && puzzleCount >= 0) {
            currentColor = targetColor;
            currentDimFactor = targetDimFactor;
        }
    }

    @Override
    public void touch() {
        super.touch();
        select();
    }

    @Override
    public void unTouch() {
        super.unTouch();
        unselect();
    }

    @Override
    public void click() {
        super.click();
        unselect();
        currentColor = 0xFFFFFFFF;
        updateToCurrentState(true);
    }

    @Override
    public void longClick() {
        super.longClick();
        unselect();
    }

    private boolean isSelected;

    private void select(){
        isSelected = true;
        currentColor =(ColorHelper.interp(COLORS[state], 0xff000000, 0.7f));
        isAnimating = false;
        isSelected = true;
        game.requestRender();
    }
    private void unselect(){
        isSelected = false;
        updateToCurrentState(true);
    }

}