package com.gartz.skwer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.gartz.skwer.tile.TileView;
import com.gartz.skwer.gui.BackgroundView;
import com.gartz.skwer.helper.ColorHelper;

/**
 * Created by Martin on 4/17/2014.
 *
 * Main Activity.
 *
 */
public class SkwerActivity extends Activity implements TileView.BaseStateListener {
    private TileView[][] tiles = new TileView[6][7];
    private BackgroundView backgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        backgroundView = (BackgroundView) findViewById(R.id.background);

        tiles[0][0] = (TileView) findViewById(R.id.tile_0_0);
        tiles[0][1] = (TileView) findViewById(R.id.tile_0_1);
        tiles[0][2] = (TileView) findViewById(R.id.tile_0_2);
        tiles[0][3] = (TileView) findViewById(R.id.tile_0_3);
        tiles[0][4] = (TileView) findViewById(R.id.tile_0_4);
        tiles[0][5] = (TileView) findViewById(R.id.tile_0_5);
        tiles[0][6] = (TileView) findViewById(R.id.tile_0_6);

        tiles[1][0] = (TileView) findViewById(R.id.tile_1_0);
        tiles[1][1] = (TileView) findViewById(R.id.tile_1_1);
        tiles[1][2] = (TileView) findViewById(R.id.tile_1_2);
        tiles[1][3] = (TileView) findViewById(R.id.tile_1_3);
        tiles[1][4] = (TileView) findViewById(R.id.tile_1_4);
        tiles[1][5] = (TileView) findViewById(R.id.tile_1_5);
        tiles[1][6] = (TileView) findViewById(R.id.tile_1_6);

        tiles[2][0] = (TileView) findViewById(R.id.tile_2_0);
        tiles[2][1] = (TileView) findViewById(R.id.tile_2_1);
        tiles[2][2] = (TileView) findViewById(R.id.tile_2_2);
        tiles[2][3] = (TileView) findViewById(R.id.tile_2_3);
        tiles[2][4] = (TileView) findViewById(R.id.tile_2_4);
        tiles[2][5] = (TileView) findViewById(R.id.tile_2_5);
        tiles[2][6] = (TileView) findViewById(R.id.tile_2_6);

        tiles[3][0] = (TileView) findViewById(R.id.tile_3_0);
        tiles[3][1] = (TileView) findViewById(R.id.tile_3_1);
        tiles[3][2] = (TileView) findViewById(R.id.tile_3_2);
        tiles[3][3] = (TileView) findViewById(R.id.tile_3_3);
        tiles[3][4] = (TileView) findViewById(R.id.tile_3_4);
        tiles[3][5] = (TileView) findViewById(R.id.tile_3_5);
        tiles[3][6] = (TileView) findViewById(R.id.tile_3_6);

        tiles[4][0] = (TileView) findViewById(R.id.tile_4_0);
        tiles[4][1] = (TileView) findViewById(R.id.tile_4_1);
        tiles[4][2] = (TileView) findViewById(R.id.tile_4_2);
        tiles[4][3] = (TileView) findViewById(R.id.tile_4_3);
        tiles[4][4] = (TileView) findViewById(R.id.tile_4_4);
        tiles[4][5] = (TileView) findViewById(R.id.tile_4_5);
        tiles[4][6] = (TileView) findViewById(R.id.tile_4_6);

        tiles[5][0] = (TileView) findViewById(R.id.tile_5_0);
        tiles[5][1] = (TileView) findViewById(R.id.tile_5_1);
        tiles[5][2] = (TileView) findViewById(R.id.tile_5_2);
        tiles[5][3] = (TileView) findViewById(R.id.tile_5_3);
        tiles[5][4] = (TileView) findViewById(R.id.tile_5_4);
        tiles[5][5] = (TileView) findViewById(R.id.tile_5_5);
        tiles[5][6] = (TileView) findViewById(R.id.tile_5_6);


        TileView.setup(this, tiles, this);
        Hints.setup(this, (android.view.ViewGroup) findViewById(R.id.hints));
        for (int i=0; i<tiles.length; i++)
            for (int j=0; j<tiles[i].length; j++)
                tiles[i][j].setupTile(i, j);
        TileView.loadlastPuzzle();
    }
    @Override
    protected void onStop() {
        super.onStop();
        TileView.saveAllTilesState();
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

            View tilesLayout = findViewById(R.id.tiles_layout);

            int width = getResources().getDisplayMetrics().widthPixels;
            tilesLayout.getLayoutParams().width = width;
            tilesLayout.getLayoutParams().height = width * tiles[0].length / tiles.length;
        }
    }

    @Override
    public void baseStateDidChange(int baseState) {
        backgroundView.setColor(ColorHelper.interp(TileView.COLORS[baseState], 0xFF000000, 0.9f));
    }

    private Handler handler = new Handler();
    public void nextPuzzleWithDelay() {
        TileView.highlightSolution(tiles);
        handler.removeCallbacks(nextPuzzleRunnable);
        handler.postDelayed(nextPuzzleRunnable,1000);
    }
    public void cancelNextPuzzle() {
        handler.removeCallbacks(nextPuzzleRunnable);
    }
    private Runnable nextPuzzleRunnable = new Runnable() {
        @Override
        public void run() {
            TileView.nextPuzzle();
        }
    };

}
