package com.gartz.skwer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by gartz on 2/1/16.
 *
 * A class for setting up the game, touch, etc.
 *
 */
public abstract class Game implements View.OnTouchListener {
    private GameGLSurfaceView surfaceView;

    private GameObject root;
    private GameObject currentTouchChild;

    private boolean ignoreClicks;

    public void setup(Context context, GameGLSurfaceView surfaceView) {
        this.surfaceView = surfaceView;

        root = new GameObject(this);
        surfaceView.renderer.setRoot(root);

        loadScene(root);
        loadState(context);
        surfaceView.setOnTouchListener(this);
    }

    protected abstract void loadScene(GameObject root);
    protected abstract void loadState(Context context);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        GameObject nextTouchChild = root.getTouchedObject(event.getX(), event.getY());

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            ignoreClicks = false;
            currentTouchChild = nextTouchChild;
            if (currentTouchChild != null) {
                currentTouchChild.touch();
                // TODO schedule long click
            }
        }
        else if (!ignoreClicks) {
            if (nextTouchChild != currentTouchChild) {
                currentTouchChild = nextTouchChild;
                if (currentTouchChild != null)
                    currentTouchChild.touch();
                // TODO cancel long click
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                ignoreClicks = true;
                if (currentTouchChild != null)
                    currentTouchChild.click();
            }
        }
        return true;
    }
}
