package com.gartz.skwer.tile;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gartz.skwer.Hints;
import com.gartz.skwer.PuzzleMaker;

/**
 * Created by Martin on 4/17/2014.
 *
 * Base Tile View
 *
 */
public abstract class TileView extends View implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    public static final int COLORS[] = {0xFFFF0080, 0xFF69D200, 0xFF0080FF};
    public int state;
    public int hintCount;
    public int puzzleCount;
    public boolean active = true;

    protected static TileView[][] tiles;
    protected static int numTilesX, numTilesY;

    protected int x,y;
    private static BaseStateListener baseStateListener;
    private static SharedPreferences preferences;
    protected static PuzzleMaker puzzleMaker;

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public static void setup(Context context, TileView[][] tiles, BaseStateListener baseStateListener){
        TileView.tiles = tiles;
        TileView.baseStateListener = baseStateListener;
        preferences = context.getSharedPreferences("", Context.MODE_PRIVATE);
        baseStateListener.baseStateDidChange(preferences.getInt("base", 0));
        puzzleMaker = new PuzzleMaker(context);
    }

    public void setupTile(int x, int y) {
        numTilesX = tiles.length;
        numTilesY = tiles[0].length;
        this.x = x;
        this.y = y;
        setOnClickListener(this);
        setOnLongClickListener(this);
        setOnTouchListener(this);
        setState(preferences.getInt(x + "_" + y, 0), 0);
    }
    public void doAction(int puzzleCountDelta) {
        if (state == 0){
            for (int i = Math.max(0, x-1); i < Math.min(numTilesX, x+2); i++)
                for (int j = Math.max(0, y-1); j < Math.min(numTilesY, y+2); j++)
                    if (i != x || j != y)
                        tiles[i][j].nextColor(puzzleCountDelta);
        }
        else if (state == 1){
            for (int i = 0; i < numTilesX; i++)
                if (i != x)
                    tiles[i][y].nextColor(puzzleCountDelta);
            for (int j = 0; j < numTilesY; j++)
                if (j != y)
                    tiles[x][j].nextColor(puzzleCountDelta);
        }
        else if (state == 2){
            for (int n = 1; x-n >= 0 && y-n >= 0; n++)
                tiles[x-n][y-n].nextColor(puzzleCountDelta);
            for (int n = 1; x-n >= 0 && y+n < numTilesY; n++)
                tiles[x-n][y+n].nextColor(puzzleCountDelta);
            for (int n = 1; x+n < numTilesX && y-n >= 0; n++)
                tiles[x+n][y-n].nextColor(puzzleCountDelta);
            for (int n = 1; x+n < numTilesX && y+n < numTilesY; n++)
                tiles[x+n][y+n].nextColor(puzzleCountDelta);
        }
    }
    public void setState(int state, int puzzleCountDelta){
        this.puzzleCount += puzzleCountDelta;
        if (puzzleCount == -3)
            puzzleCount = 0;
        int oldState = this.state;
        this.state = state%3;
        updateToCurrentState(oldState != state || puzzleCountDelta != 0);
    }
    public void addHintCount(){
        hintCount++;
    }

    private void nextColor(int puzzleCountDelta){
        setState(state + 1, puzzleCountDelta);
    }

    protected abstract void updateToCurrentState(boolean animated);
    public abstract void recalculateRendering();

    @Override
    public void onClick(View v) {
        if (active) {
            doAction(-1);
            hintCount--;
            Hints.pressedTile(state);
        }
    }
    @Override
    public boolean onLongClick(View v) {
        post(new Runnable() {
            @Override
            public void run() {
                if (y == tiles[0].length - 1) {
                    if (x < tiles.length - 1)
                        puzzleMaker.makePuzzle(tiles, x + 3, preferences.getInt("base", 0));
                    else
                        puzzleMaker.resetLastPuzzle(tiles, true);
                }
                else {
                    for (TileView[] tile : tiles)
                        for (TileView aTile : tile)
                            aTile.setState(state, -aTile.puzzleCount - 1);
                    preferences.edit().putInt("base", state).apply();
                    baseStateListener.baseStateDidChange(state);
                    puzzleMaker.clear(state, tiles);
                }
            }
        });
        return true;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public static void nextPuzzle(){
        boolean didSolvePuzzle = didSolveCurrentPuzzle();
        if (didSolvePuzzle)
            puzzleMaker.nextPuzzle(tiles);
    }
    private static boolean didSolveCurrentPuzzle(){
        if (!puzzleMaker.isInPuzzle())
            return false;
        int baseState = preferences.getInt("base", 0);

        for (TileView[] tile : tiles)
            for (TileView aTile : tile)
                if (aTile.state != baseState)
                    return false;
        return true;
    }


    public static void loadlastPuzzle() {
        if (puzzleMaker.isInPuzzle())
            puzzleMaker.resetLastPuzzle(tiles, true);
    }
    public static void saveAllTilesState(){
        for (TileView[] tile : tiles)
            for (TileView aTile : tile)
                aTile.saveState();
    }
    public void saveState(){
        preferences.edit().putInt(x + "_" + y, state).apply();
    }

    public static void highlightSolution(TileView[][] tileViews) {
        if (didSolveCurrentPuzzle())
            for (TileView[] tile : tileViews)
                for (TileView aTile : tile)
                    aTile.setState(aTile.state, -10);
    }

    public interface BaseStateListener{
        void baseStateDidChange(int baseState);
    }
}
