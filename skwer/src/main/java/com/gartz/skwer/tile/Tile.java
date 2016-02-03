package com.gartz.skwer.tile;

import com.gartz.skwer.SkwerGame;
import com.gartz.skwer.game.Game;
import com.gartz.skwer.game.GameObject;

/**
 * Created by gartz on 2/2/16.
 *
 * A representation of a skwer tile.
 *
 */
public abstract class Tile extends GameObject {
    public static final int COLORS[] = {0xFFFF0080, 0xFF69D200, 0xFF0080FF};
    public int state;
    public int hintCount;
    public int puzzleCount;
    public boolean active = true;

    protected int x;
    protected int y;

    protected SkwerGame skwerGame;

    public Tile(Game game, int x, int y, float x0, float y0) {
        super(game);
        skwerGame = (SkwerGame) game;

        this.x = x;
        this.y = y;

        setBounds(x0 - 0.5f, x0 + 0.5f, y0 - 0.5f, y0 + 0.5f);
    }

    @Override
    public void touch() {
        // TODO hint pattern!
        skwerGame.cancelNextPuzzle();
    }

    @Override
    public void click() {
        if (active) {
            doAction(-1);
            hintCount--;
//            Hints.pressedTile(state); // TODO
            skwerGame.pressedTile();
        }
    }

    @Override
    public void longClick() {
        super.longClick();
        if (y == 0) {
            if (x < SkwerGame.NUM_TILES_X - 1)
                skwerGame.makePuzzle(x + 3);
            else
                skwerGame.resetLastPuzzle(true);
        }
        else
            skwerGame.setBaseState(state);
    }

    public void doAction(int puzzleCountDelta) {
        if (state == 0){
            for (int i = Math.max(0, x-1); i < Math.min(SkwerGame.NUM_TILES_X, x+2); i++)
                for (int j = Math.max(0, y-1); j < Math.min(SkwerGame.NUM_TILES_Y, y+2); j++)
                    if (i != x || j != y)
                        skwerGame.tiles[i][j].nextColor(puzzleCountDelta);
        }
        else if (state == 1){
            for (int i = 0; i < SkwerGame.NUM_TILES_X; i++)
                if (i != x)
                    skwerGame.tiles[i][y].nextColor(puzzleCountDelta);
            for (int j = 0; j < SkwerGame.NUM_TILES_Y; j++)
                if (j != y)
                    skwerGame.tiles[x][j].nextColor(puzzleCountDelta);
        }
        else if (state == 2){
            for (int n = 1; x-n >= 0 && y-n >= 0; n++)
                skwerGame.tiles[x-n][y-n].nextColor(puzzleCountDelta);
            for (int n = 1; x-n >= 0 && y+n < SkwerGame.NUM_TILES_Y; n++)
                skwerGame.tiles[x-n][y+n].nextColor(puzzleCountDelta);
            for (int n = 1; x+n < SkwerGame.NUM_TILES_X && y-n >= 0; n++)
                skwerGame.tiles[x+n][y-n].nextColor(puzzleCountDelta);
            for (int n = 1; x+n < SkwerGame.NUM_TILES_X && y+n < SkwerGame.NUM_TILES_Y; n++)
                skwerGame.tiles[x+n][y+n].nextColor(puzzleCountDelta);
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

    protected void nextColor(int puzzleCountDelta){
        setState(state + 1, puzzleCountDelta);
    }

    protected abstract void updateToCurrentState(boolean animated);
}
