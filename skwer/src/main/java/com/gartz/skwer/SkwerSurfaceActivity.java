package com.gartz.skwer;

import android.app.Activity;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.View;

import com.gartz.skwer.game.Game;
import com.gartz.skwer.game.GameGLRenderer;
import com.gartz.skwer.game.GameGLSurfaceView;

/**
 * Created by gartz on 2/1/16.
 *
 * Main surface activity
 *
 */
public class SkwerSurfaceActivity extends Activity {
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
                game.setFrustumMatrix(projectionMatrix, width, height);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        game.saveState(this);
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

}

