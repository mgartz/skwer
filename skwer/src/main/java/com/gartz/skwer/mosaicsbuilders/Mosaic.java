package com.gartz.skwer.mosaicsbuilders;

import android.graphics.Color;
import android.graphics.Path;

import java.util.Random;

/**
 * Created by Martin on 5/3/2014.
 *
 * A simple extension to Path with color deltas.
 *
 */
public class Mosaic extends Path {
    public int baseR, baseG, baseB;
    public int deltaR, deltaG, deltaB;
    public int deltaOffset;
    private Random random = new Random();

    public void setRandomDeltas(int deviation){
        int r = random.nextInt(2 * deviation + 1);
        deltaR = deltaOffset - deviation + r;
        deltaG = deltaOffset - deviation + r;
        deltaB = deltaOffset - deviation + r;
    }
    public int getColor(){
        return Color.argb(255,
                Math.max(0, Math.min(255, baseR + deltaR)),
                Math.max(0, Math.min(255, baseG + deltaG)),
                Math.max(0, Math.min(255, baseB + deltaB)));
    }
}
