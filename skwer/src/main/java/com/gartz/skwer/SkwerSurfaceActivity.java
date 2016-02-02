package com.gartz.skwer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.gartz.skwer.tile.TileView;

/**
 * Created by gartz on 2/1/16.
 *
 * Main surface activity
 *
 */
public class SkwerSurfaceActivity extends Activity implements TileView.BaseStateListener {
    SkwerGLSurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surfaceView = new SkwerGLSurfaceView(this);
        setContentView(surfaceView);

        surfaceView.renderer.setOnSurfaceCreatedListener(new SkwerGLRenderer.OnSurfaceCreatedListener() {
            @Override
            public void onSurfaceCreated() {
                GameObject root = new GameObject() {
                    @Override
                    public void onDraw(float[] mvpMatrix) {
                        // TODO
                    }
                };
                MosaicTile child = new MosaicTile(0, 0);
                child.drawRosetta = true;
                root.addChild(child);
                MosaicTile child1 = new MosaicTile(1, 1);
                child1.drawError = true;
                root.addChild(child1);
                root.addChild(new MosaicTile(2,2));
                surfaceView.renderer.setRoot(root);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void baseStateDidChange(int baseState) {
        // TODO background
    }

}

