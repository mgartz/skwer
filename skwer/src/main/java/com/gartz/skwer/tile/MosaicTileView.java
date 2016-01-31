package com.gartz.skwer.tile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.gartz.skwer.helper.ColorHelper;
import com.gartz.skwer.mosaicsbuilders.Mosaic;
import com.gartz.skwer.mosaicsbuilders.MosaicPathsBuilder;
import com.gartz.skwer.mosaicsbuilders.CircleMosaicsBuilder;
import com.gartz.skwer.mosaicsbuilders.CrossMosaicsBuilder;
import com.gartz.skwer.mosaicsbuilders.GridMosaicsBuilder;

import java.util.List;

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
    private List<Mosaic> markedMosaics;
    private List<Mosaic> normalMosaics;
    private List<Mosaic> errorMosaics;
    private MosaicPathsBuilder gridMosaicsBuilder;
    private MosaicPathsBuilder circleMosaicsBuilder;
    private MosaicPathsBuilder crossMosaicsBuilder;

    public MosaicTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void recalculateRendering() {
        if (gridMosaicsBuilder == null)
            gridMosaicsBuilder = new GridMosaicsBuilder();
        normalMosaics = gridMosaicsBuilder.buildMosaicPaths(numMosaics, getWidth(), getHeight(), mosaicDeformation);
        if (circleMosaicsBuilder == null)
            circleMosaicsBuilder = new CircleMosaicsBuilder();
        markedMosaics = circleMosaicsBuilder.buildMosaicPaths(numMosaics, getWidth(), getHeight(), mosaicDeformation);
        if (crossMosaicsBuilder == null)
            crossMosaicsBuilder = new CrossMosaicsBuilder();
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
        List<Mosaic> paths = getCurrentPaths();

        for (Mosaic path : paths) {
            if (isAnimating)
                path.setRandomDeltas(20 * colorDeviation);
            int color = colorSum(currentColor, path.deltaR, path.deltaG, path.deltaB);
            color = ColorHelper.interp(color, 0xFF000000, currentDimFactor);
            paint.setColor(color);
            canvas.drawPath(path, paint);
        }

    }

    protected List<Mosaic> getCurrentPaths() {
        if (puzzleMaker.isInPuzzle() && puzzleCount > -3) {
            if (puzzleCount >= 3)
                return markedMosaics;
            else if (puzzleCount < 0)
                return errorMosaics;
        }
        return normalMosaics;
    }

    private int colorSum(int color, int deltaR, int deltaG, int deltaB){
        return Color.argb(Color.alpha(color),
                Math.max(0, Math.min(255, Color.red(color) + deltaR)),
                Math.max(0, Math.min(255, Color.green(color) + deltaG)),
                Math.max(0, Math.min(255, Color.blue(color) + deltaB)));
    }
}
