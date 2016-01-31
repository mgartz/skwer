package com.gartz.skwer;

import android.view.View;
import android.view.ViewGroup;

import com.gartz.skwer.tile.TileView;
import com.gartz.skwer.helper.ColorHelper;

import java.util.ArrayList;

/**
 * Created by Martin on 5/1/2014.
 *
 * Manages gui for hints.
 *
 */
public class Hints {
    private static final int HINT_BACKGROUND_PHASE_1_PERIOD = 2000;
    private static final int HINT_BACKGROUND_PHASE_2_PERIOD = 2000;
    private static final int HINT_BACKGROUND_NUM_BEATS = 1;
    private static final int HINT_COLOR = 0xFFFFFFFF;
    private static final float HINT_BACKGROND_COLOR_AMP = 1f;

    public static TileView[] tiles = new TileView[8];
    private static ArrayList<TileView> visibleTiles = new ArrayList<>();
    private static SkwerActivity activity;
    private static long hintAnimationTimeStart;

    public static void setup(SkwerActivity activity, ViewGroup hintsLayout){
        Hints.activity = activity;
        tiles[0] = (TileView) hintsLayout.findViewById(R.id.hint_tile_1);
        tiles[1] = (TileView) hintsLayout.findViewById(R.id.hint_tile_2);
        tiles[2] = (TileView) hintsLayout.findViewById(R.id.hint_tile_3);
        tiles[3] = (TileView) hintsLayout.findViewById(R.id.hint_tile_4);
        tiles[4] = (TileView) hintsLayout.findViewById(R.id.hint_tile_5);
        tiles[5] = (TileView) hintsLayout.findViewById(R.id.hint_tile_6);
        tiles[6] = (TileView) hintsLayout.findViewById(R.id.hint_tile_7);
        tiles[7] = (TileView) hintsLayout.findViewById(R.id.hint_tile_8);
    }

    public static void pressedTile(int state){
        for (int i=0; i<visibleTiles.size(); i++)
            if (visibleTiles.get(i).state == state) {
                visibleTiles.get(i).setVisibility(View.GONE);
                visibleTiles.remove(i);
                if (visibleTiles.size() == 0)
                    activity.nextPuzzleWithDelay();
                break;
            }
        if (visibleTiles.size() == 0)
            activity.nextPuzzleWithDelay();
    }

    public static void setForPuzzleStates(ArrayList<Integer> states){
        hintAnimationTimeStart = System.currentTimeMillis() - HINT_BACKGROUND_PHASE_1_PERIOD + 200;
        visibleTiles.clear();
        for (int i=0; i<states.size(); i++){
            visibleTiles.add(0,tiles[i]);
            tiles[i].setState(states.get(i),tiles[i].puzzleCount == 0 ? 1 : 0); //Puzzle count used for illumination.
            tiles[i].setVisibility(View.VISIBLE);
        }
        for (int i=states.size(); i<tiles.length; i++)
            tiles[i].setVisibility(View.GONE);

    }

    public static boolean areHintsVisible() {
        return visibleTiles.size() != 0;
    }

    public static int getHintBackgroundColor() {
        long t = System.currentTimeMillis() - hintAnimationTimeStart;
        if (t < HINT_BACKGROUND_PHASE_1_PERIOD)
            return 0x00FFFFFF;
        else if (t < HINT_BACKGROUND_PHASE_1_PERIOD + HINT_BACKGROUND_PHASE_2_PERIOD) {
            t -= HINT_BACKGROUND_PHASE_1_PERIOD;
            return ColorHelper.interp(0x00FFFFFF, HINT_COLOR,
                    (float) (HINT_BACKGROND_COLOR_AMP * (0.5 - 0.5 * Math.cos(HINT_BACKGROUND_NUM_BEATS * t * 2 * Math.PI / HINT_BACKGROUND_PHASE_2_PERIOD))));
        }
        else {
            hintAnimationTimeStart += HINT_BACKGROUND_PHASE_1_PERIOD + HINT_BACKGROUND_PHASE_2_PERIOD;
            return 0x00FFFFFF;
        }
    }

}
