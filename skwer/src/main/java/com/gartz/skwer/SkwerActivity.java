package com.gartz.skwer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.gartz.skwer.tile.MosaicTileView;
import com.gartz.skwer.tile.TileView;
import com.gartz.skwer.gui.BackgroundView;
import com.gartz.skwer.helper.ColorHelper;

/**
 * Created by Martin on 4/17/2014.
 *
 * Main Activity.
 *
 */
public class SkwerActivity extends Activity implements TileView.BaseStateListener, TileView.TileClickListener {
    private TileView[][] tiles = new TileView[4][5];
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

        tiles[1][0] = (TileView) findViewById(R.id.tile_1_0);
        tiles[1][1] = (TileView) findViewById(R.id.tile_1_1);
        tiles[1][2] = (TileView) findViewById(R.id.tile_1_2);
        tiles[1][3] = (TileView) findViewById(R.id.tile_1_3);
        tiles[1][4] = (TileView) findViewById(R.id.tile_1_4);

        tiles[2][0] = (TileView) findViewById(R.id.tile_2_0);
        tiles[2][1] = (TileView) findViewById(R.id.tile_2_1);
        tiles[2][2] = (TileView) findViewById(R.id.tile_2_2);
        tiles[2][3] = (TileView) findViewById(R.id.tile_2_3);
        tiles[2][4] = (TileView) findViewById(R.id.tile_2_4);

        tiles[3][0] = (TileView) findViewById(R.id.tile_3_0);
        tiles[3][1] = (TileView) findViewById(R.id.tile_3_1);
        tiles[3][2] = (TileView) findViewById(R.id.tile_3_2);
        tiles[3][3] = (TileView) findViewById(R.id.tile_3_3);
        tiles[3][4] = (TileView) findViewById(R.id.tile_3_4);



        TileView.setup(this, tiles, this, this);
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

            int width = getResources().getDisplayMetrics().widthPixels * 5 / 6;
            tilesLayout.getLayoutParams().width = width;
            tilesLayout.getLayoutParams().height = width * 5 / 4;
        }
    }

    @Override
    public void baseStateDidChange(int baseState) {
        backgroundView.setColor(ColorHelper.interp(TileView.COLORS[baseState], 0xFF000000, 0.9f));
    }
    @Override
    public void tileDidClick(int state) {

    }

    private Handler handler = new Handler();
    public void nextPuzzleWithDelay() {
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
