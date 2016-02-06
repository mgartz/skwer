package com.gartz.skwer;

import android.content.Context;
import android.content.SharedPreferences;

import com.gartz.skwer.game.Game;
import com.gartz.skwer.game.GameObject;
import com.gartz.skwer.tile.MosaicTile;
import com.gartz.skwer.tile.Tile;

/**
 * Created by gartz on 2/1/16.
 *
 * An implementation for game that manages the skwer logic.
 *
 */
public class SkwerGame extends Game {
    public static final int NUM_TILES_X = 6;
    public static final int NUM_TILES_Y = 9;
    public static final float GAP = 0.15f;

    public final Tile[][] tiles = new Tile[NUM_TILES_X][NUM_TILES_Y];

    private PuzzleMaker puzzleMaker;
    private Hints hints;

    private SharedPreferences preferences;

    private int baseState;

    @Override
    protected void loadScene(GameObject root) {
        hints = new Hints();
        makeGameTiles(root);
    }

    @Override
    protected void loadState(Context context) {
        puzzleMaker = new PuzzleMaker(context);
        preferences = context.getSharedPreferences("", Context.MODE_PRIVATE);
        baseState = preferences.getInt("base", 0);

        if (isInPuzzle())
            puzzleMaker.resetLastPuzzle(tiles, true);
        else
            loadTilesStates();
    }

    @Override
    public void saveState(Context context) {
        saveTilesStates();
    }

    private void loadTilesStates() {
        for (int i=0; i<NUM_TILES_X; i++)
            for (int j=0; j<NUM_TILES_Y; j++)
                tiles[i][j].setState(preferences.getInt("tile" + i + "_" + j, 0), 0, false);
    }
    private void saveTilesStates() {
        SharedPreferences.Editor editor = preferences.edit();
        for (int i=0; i<NUM_TILES_X; i++)
            for (int j=0; j<NUM_TILES_Y; j++)
                editor.putInt("tile" + i + "_" + j, tiles[i][j].state);
        editor.apply();
    }

    private void makeGameTiles(GameObject root) {
        for (int i=0; i< NUM_TILES_X; i++)
            for (int j=0; j< NUM_TILES_Y; j++) {
                float x = i * (1 + GAP) - (NUM_TILES_X - 1) * (1 + GAP) / 2f;
                float y = j * (1 + GAP) - (NUM_TILES_Y - 1) * (1 + GAP) / 2f;
                tiles[i][j] = new MosaicTile(this, i, j, x, y);
                root.addChild(tiles[i][j]);
            }
    }

    public void setBaseState(int state) {
        baseState = state;
        puzzleMaker.clear(state, tiles);
        for (Tile[] row : tiles)
            for (Tile tile : row)
                tile.setState(state, -tile.puzzleCount - 1, false);
        preferences.edit().putInt("base", state).apply();
    }

    public void pressedTile() {
        if (isInPuzzle() && didSolvePuzzle()) {
            highlightSolution();
            cancelNextPuzzle();
            handler.postDelayed(nextPuzzleRunnable,1000);
        }
    }

    // Puzzle
    public boolean isInPuzzle() {
        return puzzleMaker.isInPuzzle();
    }
    private boolean didSolvePuzzle() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile.state != baseState)
                    return false;
        return true;
    }

    private void highlightSolution() {
        if (didSolvePuzzle())
            for (Tile[] row : tiles)
                for (Tile tile : row)
                    tile.setState(tile.state, -10, false);
    }

    public void makePuzzle(int numMoves) {
        puzzleMaker.makePuzzle(tiles, numMoves, baseState);
    }
    public void resetLastPuzzle(boolean addRepeat) {
        puzzleMaker.resetLastPuzzle(tiles, addRepeat);
        hints.setForPuzzleStates();
    }
    public void cancelNextPuzzle() {
        handler.removeCallbacks(nextPuzzleRunnable);
    }
    private Runnable nextPuzzleRunnable = new Runnable() {
        @Override
        public void run() {
            if (didSolvePuzzle())
                puzzleMaker.nextPuzzle(tiles);
        }
    };

    public int getHintColor() {
        return hints.getHintBackgroundColor();
    }
}
