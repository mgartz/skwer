package com.gartz.skwer.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gartz.skwer.helper.ColorHelper;

/**
 * Created by Martin on 5/4/2014.
 *
 * Allows effectas based on tile selection.
 *
 */
public class BackgroundView extends RelativeLayout {
    private static final int TRANSITION_PERIOD = 300;
    private int currentColor;
    private int targetColor;
    private long transitionStartTime;

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setColor(int color){
        targetColor = color;
        transitionStartTime = System.currentTimeMillis();
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long t = System.currentTimeMillis() - transitionStartTime;
        if (t < TRANSITION_PERIOD){
            setBackgroundColor(ColorHelper.interp(currentColor, targetColor, 1f * t / TRANSITION_PERIOD));
            invalidate();
        }
        else {
            currentColor = targetColor;
            setBackgroundColor(currentColor);
        }
    }
}
