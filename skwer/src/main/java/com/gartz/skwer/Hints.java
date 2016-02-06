package com.gartz.skwer;

import com.gartz.skwer.helper.ColorHelper;

/**
 * Created by Martin on 5/1/2014.
 *
 * Manages hints timing.
 *
 */
public class Hints {
    private static final int HINT_BACKGROUND_PHASE_1_PERIOD = 2000;
    private static final int HINT_BACKGROUND_PHASE_2_PERIOD = 2000;
    private static final int HINT_BACKGROUND_NUM_BEATS = 1;
    private static final int HINT_COLOR = 0xFFFFFFFF;
    private static final float HINT_BACKGROND_COLOR_AMP = 1f;

    private static long hintAnimationTimeStart;

    public void setForPuzzleStates(){
        hintAnimationTimeStart = System.currentTimeMillis() - HINT_BACKGROUND_PHASE_1_PERIOD + 200;
    }

    public int getHintBackgroundColor() {
        long t = System.currentTimeMillis() - hintAnimationTimeStart;
        if (t < HINT_BACKGROUND_PHASE_1_PERIOD)
            return 0xFF000000;
        else if (t < HINT_BACKGROUND_PHASE_1_PERIOD + HINT_BACKGROUND_PHASE_2_PERIOD) {
            t -= HINT_BACKGROUND_PHASE_1_PERIOD;
            double rx = 0.5f * (1 - Math.cos(HINT_BACKGROUND_NUM_BEATS * t * 2 * Math.PI / HINT_BACKGROUND_PHASE_2_PERIOD));
            float x = (float) (HINT_BACKGROND_COLOR_AMP * rx);
            return ColorHelper.interp(0xFF000000, HINT_COLOR, x);
        }
        else {
            hintAnimationTimeStart += HINT_BACKGROUND_PHASE_1_PERIOD + HINT_BACKGROUND_PHASE_2_PERIOD;
            return 0xFF000000;
        }
    }

}
