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

    protected int i;
    protected int j;

    protected SkwerGame skwerGame;

    public Tile(Game game, int i, int j, float x, float y) {
        super(game);
        skwerGame = (SkwerGame) game;

        this.i = i;
        this.j = j;

        setBounds(x - 0.5f, x + 0.5f, y - 0.5f, y + 0.5f);
    }

    @Override
    public void touch() {
        skwerGame.cancelNextPuzzle();
    }

    @Override
    public void click() {
        if (active) {
            nextStateOnPattern(-1);
            hintCount--;
            skwerGame.pressedTile();
        }
    }

    @Override
    public void longClick() {
        super.longClick();
        if (j == 0) {
            if (i < SkwerGame.NUM_TILES_X - 1)
                skwerGame.makePuzzle(i + 3);
            else
                skwerGame.resetLastPuzzle(true);
        }
        else
            skwerGame.setBaseState(state);
    }

    public void nextStateOnPattern(final int puzzleCountDelta) {
        doCommandOnPattern(new Command() {
            @Override
            public void call(Tile tile) {
                tile.nextState(puzzleCountDelta);
            }
        });
    }
    protected void doCommandOnPattern(Command command) {
        if (state == 0){
            for (int i = Math.max(0, this.i - 1); i < Math.min(SkwerGame.NUM_TILES_X, this.i + 2); i++)
                for (int j = Math.max(0, this.j - 1); j < Math.min(SkwerGame.NUM_TILES_Y, this.j + 2); j++)
                    if (i != this.i || j != this.j)
                        command.call(skwerGame.tiles[i][j]);
        }
        else if (state == 1){
            for (int i = 0; i < SkwerGame.NUM_TILES_X; i++)
                if (i != this.i)
                    command.call(skwerGame.tiles[i][j]);
            for (int j = 0; j < SkwerGame.NUM_TILES_Y; j++)
                if (j != this.j)
                    command.call(skwerGame.tiles[i][j]);
        }
        else if (state == 2){
            for (int n = 1; i - n >= 0 && j - n >= 0; n++)
                command.call(skwerGame.tiles[i - n][j - n]);
            for (int n = 1; i - n >= 0 && j + n < SkwerGame.NUM_TILES_Y; n++)
                command.call(skwerGame.tiles[i - n][j + n]);
            for (int n = 1; i + n < SkwerGame.NUM_TILES_X && j - n >= 0; n++)
                command.call(skwerGame.tiles[i + n][j - n]);
            for (int n = 1; i + n < SkwerGame.NUM_TILES_X && j + n < SkwerGame.NUM_TILES_Y; n++)
                command.call(skwerGame.tiles[i + n][j + n]);
        }
    }
    public void setState(int state, int puzzleCountDelta, boolean isUserAction){
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

    protected void nextState(int puzzleCountDelta){
        setState(state + 1, puzzleCountDelta, true);
    }

    protected abstract void updateToCurrentState(boolean animated);

    public interface Command {
        void call(Tile tile);
    }
}
