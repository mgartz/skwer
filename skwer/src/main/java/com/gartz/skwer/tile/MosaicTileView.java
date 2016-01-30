package com.gartz.skwer.tile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.gartz.skwer.Hints;
import com.gartz.skwer.mosaicsbuilders.Mosaic;
import com.gartz.skwer.mosaicsbuilders.MosaicPathsBuilder;
import com.gartz.skwer.mosaicsbuilders.CircleMosaicsBuilder;
import com.gartz.skwer.mosaicsbuilders.CrossMosaicsBuilder;
import com.gartz.skwer.mosaicsbuilders.GridMosaicsBuilder;

import java.util.ArrayList;

/**
 * Created by Martin on 5/1/2014.
 *
 * Draws mosaic paths based on mosaic paths builder and parameters.
 *
 */
public class MosaicTileView extends AnimatedTileView {
    protected static int gapSize = 4;
    protected static int colorDeviation = 3;
    protected static int mosaicDeformation = 2;
    protected static int numMosaics = 5;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ArrayList<Mosaic> markedMosaics = new ArrayList<>();
    private ArrayList<Mosaic> normalMosaics = new ArrayList<>();
    private ArrayList<Mosaic> errorMosaics = new ArrayList<>();
    private MosaicPathsBuilder gridMosaicsBuilder = new GridMosaicsBuilder();
    private MosaicPathsBuilder circleMosaicsBuilder = new CircleMosaicsBuilder();
    private MosaicPathsBuilder crossMosaicsBuilder = new CrossMosaicsBuilder();


    public MosaicTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void recalculateRendering() {
        normalMosaics = gridMosaicsBuilder.buildMosaicPaths(numMosaics, getWidth(), getHeight(), mosaicDeformation);
        markedMosaics = circleMosaicsBuilder.buildMosaicPaths(numMosaics, getWidth(), getHeight(), mosaicDeformation);
        errorMosaics = crossMosaicsBuilder.buildMosaicPaths(numMosaics, getWidth(), getHeight(), mosaicDeformation);
        for (Mosaic path : normalMosaics)
            path.setRandomDeltas(20 * colorDeviation);
        for (Mosaic path : markedMosaics)
            path.setRandomDeltas(20 * colorDeviation);
        for (Mosaic path : errorMosaics)
            path.setRandomDeltas(20 * colorDeviation);

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        recalculateRendering();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale = (1 - gapSize/25f);
        canvas.scale(scale,scale,getWidth()/2, getHeight()/2);
        ArrayList<Mosaic> paths = normalMosaics;
        if (Hints.areHintsVisible()) {
            if (puzzleCount >= 3)
                paths = markedMosaics;
            else if (puzzleCount < 0)
                paths = errorMosaics;
        }

        for (Mosaic path : paths) {
            if (isAnimating)
                path.setRandomDeltas(20 * colorDeviation);
            int color = colorSum(currentColor, path.deltaR, path.deltaG, path.deltaB);
            paint.setColor(color);
            canvas.drawPath(path, paint);
        }

    }

    private int colorSum(int color, int deltaR, int deltaG, int deltaB){
        return Color.argb(Color.alpha(color),
                Math.max(0, Math.min(255, Color.red(color) + deltaR)),
                Math.max(0, Math.min(255, Color.green(color) + deltaG)),
                Math.max(0, Math.min(255, Color.blue(color) + deltaB)));
    }
}
