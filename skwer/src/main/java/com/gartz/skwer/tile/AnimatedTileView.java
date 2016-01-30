package com.gartz.skwer.tile;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gartz.skwer.Hints;
import com.gartz.skwer.helper.ColorHelper;
import com.gartz.skwer.SkwerActivity;

/**
 * Created by Martin on 4/17/2014.
 *
 * Extends TileView and provides animation via color interpolation
 *
 */
public abstract class AnimatedTileView extends TileView {
    protected int currentColor = 0xFFFFFFFF;
    private int targetColor;
    private int origColor;
    protected boolean isAnimating;
    private long animationStartTime;
    private long animationPeriod;

    public AnimatedTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void updateToCurrentState(boolean animated) {
        if (animated) {
            targetColor = COLORS[state];
            origColor = currentColor;
            isAnimating = true;
            animationStartTime = System.currentTimeMillis();
            animationPeriod = 300;
        }
        invalidate();
    }

    @Override
    public void addHintCount() {
        super.addHintCount();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isAnimating){
            float t = 1f*(System.currentTimeMillis() - animationStartTime)/animationPeriod;
            if (t >= 1) {
                isAnimating = false;
                t = 1;
            }
            currentColor = ColorHelper.interp(origColor, targetColor, t);
            invalidate();
        }
        if (hintCount > 0){
            setBackgroundColor(Hints.getHintBackgroundColor());
            invalidate();
        }
        else
            setBackgroundColor(0x00000000);

        if (!isAnimating && hintCount == 0 && !isSelected && puzzleCount >= 0)
            currentColor = COLORS[state];
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        currentColor = 0xFFFFFFFF;
        updateToCurrentState(true);
    }

    private boolean isSelected;
    private boolean touchCancelled;


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (getContext() != null && getContext() instanceof SkwerActivity)
                ((SkwerActivity)getContext()).cancelNextPuzzle();
            select();
            touchCancelled = false;
        }
        else if (touchCancelled)
            return true;
        else if (event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            if (isSelected)
                unselect();
        }
        else if (event.getActionMasked() == MotionEvent.ACTION_MOVE){
            if (isSelected && (event.getX() < 0 || event.getX() > getWidth() || event.getY() < 0 || event.getY() > getHeight())) {
                unselect();
            }
            else if (!isSelected && (event.getX() > 0 && event.getX() < getWidth() && event.getY() > 0 && event.getY() < getHeight()))
                select();
        }
        else if (event.getActionMasked() == MotionEvent.ACTION_UP){
            if (isSelected) {
                removeCallbacks(longClick);
                v.performClick();
            }
        }
        return true;
    }
    private void select(){
        isSelected = true;
        currentColor =(ColorHelper.interp(COLORS[state], 0xff000000, 0.7f));
        isAnimating = false;
        isSelected = true;
        removeCallbacks(longClick);
        postDelayed(longClick, 500);
        invalidate();
    }
    private void unselect(){
        isSelected = false;
        updateToCurrentState(true);
        removeCallbacks(longClick);
    }
    private Runnable longClick = new Runnable() {
        @Override
        public void run() {
            touchCancelled = true;
            unselect();
            performLongClick();
        }
    };
}
