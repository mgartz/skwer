package com.gartz.skwer;

import android.content.Context;
import android.content.SharedPreferences;

import com.gartz.skwer.tile.TileView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Martin on 4/17/2014.
 *
 * Makes puzzles
 *
 */
public class PuzzleMaker {
    private SharedPreferences preferences;
    private int lastPuzzleBase;
    private ArrayList<Integer> lastPuzzle = new ArrayList<>();
    private ArrayList<Integer> lastPuzzleStates = new ArrayList<>();
    private static Random random = new Random();
    private int numRepeats;
    private boolean inPuzzle;

    public PuzzleMaker(Context context){
        preferences = context.getSharedPreferences("puzzle", Context.MODE_PRIVATE);
        if (preferences.getBoolean("was_in_puzzle", false)){
            inPuzzle = true;
            int numMoves = preferences.getInt("moves",0);
            lastPuzzleBase = preferences.getInt("base_state",0);
            lastPuzzle.clear();
            lastPuzzleStates.clear();
            for (int i=0; i<numMoves; i++) {
                lastPuzzleStates.add(preferences.getInt("state_" + i, 0));
                lastPuzzle.add(preferences.getInt("puzzle_" + (i * 2), 0));
                lastPuzzle.add(preferences.getInt("puzzle_" + (i*2+1), 0));
            }
        }
    }

    public void makePuzzle(TileView[][] tiles, int numMoves, int baseState){
        Map<String, Integer> counts = new HashMap<>();
        int sizeX = tiles.length;
        int sizeY = tiles[0].length;

        startedPuzzle(tiles);
        SharedPreferences.Editor editor = preferences.edit();
        numRepeats = 0;
        lastPuzzleBase = baseState;
        lastPuzzle.clear();
        lastPuzzleStates.clear();
        editor.putInt("base_state", baseState);
        editor.putInt("moves", numMoves);

        for (TileView[] tile : tiles)
            for (TileView aTile : tile) {
                aTile.setState(baseState, -aTile.puzzleCount);
                aTile.hintCount = 0;
            }
        for (int i=0; i<numMoves; i++){
            int x = random.nextInt(sizeX);
            int y = random.nextInt(sizeY);
            Integer lastCount = counts.get(x + "" + y + "" + tiles[x][y].state);
            lastCount = lastCount == null ? 0 : lastCount;
            if (lastCount < 2 && tiles[x][y].active) {
                lastPuzzle.add(x);
                lastPuzzle.add(y);
                lastPuzzleStates.add(tiles[x][y].state);
                counts.put(x + "" + y + "" + tiles[x][y].state, lastCount + 1);
                editor.putInt("puzzle_" + (i * 2), x);
                editor.putInt("puzzle_" + (i * 2 + 1), y);
                editor.putInt("state_" + i, tiles[x][y].state);

                tiles[x][y].doAction(1);
                tiles[x][y].doAction(0);
            }
            else
                i--;
        }
        editor.apply();
        Hints.setForPuzzleStates(lastPuzzleStates);
    }

    public void clear(int baseState, TileView[][] tiles){
        endedPuzzle(tiles);
        lastPuzzle.clear();
        lastPuzzleStates.clear();
        lastPuzzleBase = baseState;
        for (TileView[] tile : tiles)
            for (TileView aTile : tile)
                aTile.hintCount = 0;
        Hints.setForPuzzleStates(lastPuzzleStates);
    }

    public void resetLastPuzzle(TileView[][] tiles, boolean addRepeat) {
        startedPuzzle(tiles);
        if (addRepeat)
            numRepeats++;

        for (TileView[] tile : tiles)
            for (TileView aTile : tile) {
                aTile.setState(lastPuzzleBase,-aTile.puzzleCount);
                aTile.hintCount = 0;
            }
        for (int i=0; i<lastPuzzle.size()/2; i++){
            int x = lastPuzzle.get(i*2);
            int y = lastPuzzle.get(i*2+1);

            tiles[x][y].doAction(1);
            tiles[x][y].doAction(0);

            if (numRepeats >= 2)
                tiles[x][y].addHintCount();
        }
        Hints.setForPuzzleStates(lastPuzzleStates);
    }

    public void nextPuzzle(TileView[][] tileViews) {
        makePuzzle(tileViews, lastPuzzleStates.size(), lastPuzzleBase);
    }

    public void startedPuzzle(TileView[][] tiles){
        inPuzzle = true;
        for (int i=0; i<tiles.length; i++)
            for (int j=0; j<tiles[0].length; j++)
                if (i == 0 || i == tiles.length-1 || j == 0 || j == tiles[0].length - 1)
                    tiles[i][j].active = false;
        preferences.edit().putBoolean("was_in_puzzle", true).apply();
    }
    public void endedPuzzle(TileView[][] tiles){
        inPuzzle = false;
        for (int i=0; i<tiles.length; i++)
            for (int j=0; j<tiles[0].length; j++) {
                tiles[i][j].active = true;
                tiles[i][j].hintCount = 0;
                tiles[i][j].setState(tiles[i][j].state, -1);
            }
        preferences.edit().putBoolean("was_in_puzzle", false).apply();
    }

    public boolean isInPuzzle() {
        return inPuzzle;
    }

}
