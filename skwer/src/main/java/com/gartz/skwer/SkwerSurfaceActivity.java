package com.gartz.skwer;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.view.View;

import com.gartz.skwer.tile.TileView;

/**
 * Created by gartz on 2/1/16.
 *
 * Main surface activity
 *
 */
public class SkwerSurfaceActivity extends Activity implements TileView.BaseStateListener {
    GameGLSurfaceView surfaceView;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surfaceView = new GameGLSurfaceView(this);
        setContentView(surfaceView);

        game = new SkwerGame();

        surfaceView.renderer.setOnSurfaceCreatedListener(new GameGLRenderer.OnSurfaceCreatedListener() {
            @Override
            public void onSurfaceCreated() {
                game.setup(SkwerSurfaceActivity.this, surfaceView);
            }
        });
        surfaceView.renderer.setOnSurfaceChangedListener(new GameGLRenderer.OnSurfaceChangedListener() {
            @Override
            public void onSurfaceChanged(float[] projectionMatrix, int width, int height) {
                GLES20.glViewport(0, 0, width, height);
                float ratio = (float) height / width;
                float s = SkwerGame.NUM_TILES_X * (1 + SkwerGame.GAP) / 2f;
                Matrix.frustumM(projectionMatrix, 0, -s, s, -ratio * s, ratio * s, 1, 2);
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

