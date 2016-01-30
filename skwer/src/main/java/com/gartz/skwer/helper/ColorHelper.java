package com.gartz.skwer.helper;

import android.graphics.Color;

/**
 * Created by Martin on 4/17/2014.
 *
 * Helps with interpolation and other misc methods.
 *
 */
public class ColorHelper {

    public static int interp(int c0, int c1, float x){
        int a = (int) (Color.alpha(c0) * (1-x) + Color.alpha(c1) * x);
        int r = (int) (Color.red(c0) * (1-x) + Color.red(c1) * x);
        int g = (int) (Color.green(c0) * (1-x) + Color.green(c1) * x);
        int b = (int) (Color.blue(c0) * (1-x) + Color.blue(c1) * x);

        return Color.argb(a, r, g, b);
    }

}
