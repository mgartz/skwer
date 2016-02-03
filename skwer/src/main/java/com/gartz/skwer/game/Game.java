package com.gartz.skwer.game;

import android.content.Context;
import android.opengl.Matrix;
import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import com.gartz.skwer.SkwerGame;

/**
 * Created by gartz on 2/1/16.
 *
 * A class for setting up the game, touch, etc.
 *
 */
public abstract class Game implements View.OnTouchListener {
    private GameGLSurfaceView surfaceView;

    private GameObject root;

    private static final int MAX_TOUCHES = 5;
    private GameObject[] currentTouchChild = new GameObject[MAX_TOUCHES];
    private boolean[] ignoreClicks = new boolean[MAX_TOUCHES];
    private Runnable[] longClickRunnable = new Runnable[MAX_TOUCHES];

    private float[] viewportSize = new float[2];
    private float[] frustumBounds = new float[4];

    protected Handler handler = new Handler();

    public void setup(Context context, GameGLSurfaceView surfaceView) {
        this.surfaceView = surfaceView;

        root = new GameObject(this);
        surfaceView.renderer.setRoot(root);

        loadScene(root);
        loadState(context);
        surfaceView.setOnTouchListener(this);
        for (int i=0; i<longClickRunnable.length; i++)
            longClickRunnable[i] = longClickRunnable(i);
    }
    public void setFrustumMatrix(float[] projectionMatrix, int width, int height) {
        float ratio = (float) height / width;
        float s = SkwerGame.NUM_TILES_X * (1 + SkwerGame.GAP) / 2f;
        viewportSize[0] = width;
        viewportSize[1] = height;
        frustumBounds[0] = -s;
        frustumBounds[1] = s;
        frustumBounds[2] = -ratio * s;
        frustumBounds[3] = ratio * s;
        Matrix.frustumM(projectionMatrix, 0, frustumBounds[0], frustumBounds[1], frustumBounds[2], frustumBounds[3], 1, 2);
    }

    protected abstract void loadScene(GameObject root);
    protected abstract void loadState(Context context);
    public abstract void saveState(Context context);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        for (int i=0; i<event.getPointerCount(); i++) {
            int id = event.getPointerId(i);
            if (id < MAX_TOUCHES) {
                float x = frustumBounds[0] + (frustumBounds[1] - frustumBounds[0]) * event.getX(i) / viewportSize[0];
                float y = frustumBounds[3] + (frustumBounds[2] - frustumBounds[3]) * event.getY(i) / viewportSize[1];
                GameObject nextTouchChild = root.getTouchedObject(x, y);

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                        || (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN && i == event.getActionIndex())) {
                    ignoreClicks[id] = false;
                    currentTouchChild[id] = nextTouchChild;
                    if (currentTouchChild[id] != null) {
                        currentTouchChild[id].touch();
                        handler.postDelayed(longClickRunnable[id], 500);
                    }
                } else if (!ignoreClicks[id]) {
                    if (nextTouchChild != currentTouchChild[id]) {
                        if (currentTouchChild[id] != null)
                            currentTouchChild[id].unTouch();
                        currentTouchChild[id] = nextTouchChild;
                        if (currentTouchChild[id] != null)
                            currentTouchChild[id].touch();
                        handler.removeCallbacks(longClickRunnable[id]);
                    }
                    if (event.getActionMasked() == MotionEvent.ACTION_UP
                            || (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP && i == event.getActionIndex())) {
                        handler.removeCallbacks(longClickRunnable[id]);
                        ignoreClicks[id] = true;
                        if (currentTouchChild[id] != null)
                            currentTouchChild[id].click();
                    }
                }
            }
        }
        return true;
    }
    private Runnable longClickRunnable(final int index) {
        return new Runnable() {
            @Override
            public void run() {
                ignoreClicks[index] = true;
                if (currentTouchChild[index] != null) {
                    surfaceView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    currentTouchChild[index].longClick();
                }
            }
        };
    }

    public void requestRender() {
        surfaceView.requestRender();
    }
}
