package com.gartz.skwer;

import android.content.Context;

/**
 * Created by gartz on 2/1/16.
 *
 * An implementation for game that manages the skwer logic.
 *
 */
public class SkwerGame extends Game {
    public static final int NUM_TILES_X = 6;
    public static final int NUM_TILES_Y = 7;
    public static final float GAP = 0.15f;

    public final MosaicTile[][] tiles = new MosaicTile[NUM_TILES_X][NUM_TILES_Y];

    @Override
    protected void loadScene(GameObject root) {
        makeGameTiles(root);
        // TODO load the hints!
    }

    @Override
    protected void loadState(Context context) {
        // TODO load the state!
    }

    private void makeGameTiles(GameObject root) {
        for (int i=0; i< NUM_TILES_X; i++)
            for (int j=0; j< NUM_TILES_Y; j++) {
                tiles[i][j] = new MosaicTile(this, i * (1 + GAP) - (NUM_TILES_X - 1) * (1 + GAP) / 2f, j * (1 + GAP) - (NUM_TILES_Y - 1) * (1 + GAP) / 2f);
                root.addChild(tiles[i][j]);
            }
    }
}
