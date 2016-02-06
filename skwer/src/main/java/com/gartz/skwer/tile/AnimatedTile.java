package com.gartz.skwer.tile;

import com.gartz.skwer.game.Game;
import com.gartz.skwer.SkwerGame;
import com.gartz.skwer.helper.ColorHelper;
import com.gartz.skwer.mosaic.Mosaic;
import com.gartz.skwer.mosaic.Polygon;

import java.util.ArrayList;

/**
 * Created by gartz on 2/2/16.
 *
 * An extension of tile that wraps the animation logic.
 *
 */
public class AnimatedTile extends Tile {
    public static final int ANIMATION_PERIOD = 400;

    protected int currentColor = 0xFF000000;
    private int targetColor;
    private int origColor;

    protected boolean isAnimating;
    private long animationStartTime;

    protected boolean isSelected;

    protected float currentDimFactor;
    private float origDimFactor;
    private float targetDimFactor;

    private Mosaic background;

    public AnimatedTile(Game game, int i, int j, float x, float y) {
        super(game, i, j, x, y);

        setupBackground(x, y);
    }

    private void setupBackground(float x, float y) {
        background = new Mosaic();

        Polygon backgroundPoly = new Polygon();
        float size = 0.5f * (1 + SkwerGame.GAP);
        backgroundPoly.addVertex(x - size, y - size);
        backgroundPoly.addVertex(x - size, y + size);
        backgroundPoly.addVertex(x + size, y + size);
        backgroundPoly.addVertex(x + size, y - size);
        ArrayList<Polygon> polygons = new ArrayList<>();
        polygons.add(backgroundPoly);

        background.setPolygons(polygons);
        background.setColor(0xFFFFFFFF, 0);
        background.randomizeColorDeltas(0);
    }

    @Override
    protected void updateToCurrentState(boolean animated) {
        targetColor = COLORS[state];
        if (skwerGame.isInPuzzle()) {
            if (active)
                targetDimFactor = puzzleCount == 0 ? 0.3f : 0;
            else
                targetDimFactor = puzzleCount == 0 ? 0.9f : 0.6f;
        }
        else {
            float xDist = Math.abs(i - SkwerGame.NUM_TILES_X / 2f + 0.5f) / SkwerGame.NUM_TILES_X;
            float yDist = Math.abs(j - SkwerGame.NUM_TILES_Y / 2f + 0.5f) / SkwerGame.NUM_TILES_Y;
            targetDimFactor = 0.75f * (xDist + yDist);
        }
        if (animated) {
            origColor = currentColor;
            origDimFactor = currentDimFactor;
            isAnimating = true;
            animationStartTime = System.currentTimeMillis();
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
            float t = 1f * (System.currentTimeMillis() - animationStartTime) / ANIMATION_PERIOD;
            if (t >= 1) {
                isAnimating = false;
                t = 1;
            }
            currentColor = ColorHelper.interp(origColor, targetColor, t);
            currentDimFactor = origDimFactor * (1-t) + targetDimFactor * t;
            game.requestRender();
        }

        if (hintCount > 0) {
            background.setColor(skwerGame.getHintColor(), 0);
            background.draw(mvpMatrix);
            game.requestRender();
        }

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

    private void select(){
        isSelected = true;
        currentColor = ColorHelper.interp(COLORS[state], 0xFF000000, 0.7f);
        isAnimating = false;
        game.requestRender();
    }
    private void unselect(){
        isSelected = false;
        updateToCurrentState(true);
    }

}
