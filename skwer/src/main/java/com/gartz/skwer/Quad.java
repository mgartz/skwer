package com.gartz.skwer;

import android.graphics.PointF;

import java.util.Arrays;

/**
 * Created by gartz on 2/1/16.
 *
 * A quad mosaic element.
 *
 */
public class Quad {
    public static byte COORDS_PER_VERTEX = 3;
    public static byte VERTICES_PER_QUAD = 4;
    public static byte CHANNELS_PER_COLOR = 4;
    public static byte COLORS_PER_QUAD = 4;

    Mosaic mosaic;
    int offset;

    private int color;
    private PointF[] corners = new PointF[4];

    private boolean cornersChanged = true;
    private boolean colorChanged = true;

    public Quad() {
        color = 0xFFFF0080;
        for (int i=0; i<corners.length; i++)
            corners[i] = new PointF();
    }

    public void update() {
        if (cornersChanged) {
            for (int i = 0; i < corners.length; i++) {
                mosaic.quadVertices[offset * COORDS_PER_VERTEX * VERTICES_PER_QUAD + i * COORDS_PER_VERTEX] = corners[i].x;
                mosaic.quadVertices[offset * COORDS_PER_VERTEX * VERTICES_PER_QUAD + i * COORDS_PER_VERTEX + 1] = corners[i].y;
                mosaic.quadVertices[offset * COORDS_PER_VERTEX * VERTICES_PER_QUAD + i * COORDS_PER_VERTEX + 2] = 0;
            }
            cornersChanged = false;
        }
        if (colorChanged) {
            int rOffset = offset * CHANNELS_PER_COLOR * COLORS_PER_QUAD;
            mosaic.quadColors[rOffset] = 1f * ((color >> 0x10) & 0xFF) / 0xFF;
            mosaic.quadColors[rOffset + 1] = 1f * ((color >> 0x8) & 0xFF) / 0xFF;
            mosaic.quadColors[rOffset + 2] = 1f * (color & 0xFF) / 0xFF;
            mosaic.quadColors[rOffset + 3] = 1f * ((color >> 0x18) & 0xFF) / 0xFF;
//            Random random = new Random();
            for (int i=0; i<COLORS_PER_QUAD; i++)
                for (int j=0; j<CHANNELS_PER_COLOR; j++) {
//                    mosaic.quadColors[rOffset + CHANNELS_PER_COLOR * i + j] = mosaic.quadColors[rOffset + j] + (0.15f - random.nextFloat() * 0.3f); // Randomize colors a bit.
                    mosaic.quadColors[rOffset + CHANNELS_PER_COLOR * i + j] = mosaic.quadColors[rOffset + j];
                }
            colorChanged = false;
        }
    }

    public void setCorner(int index, float x, float y) {
        if (corners[index].x != x || corners[index].y != y) {
            corners[index].x = x;
            corners[index].y = y;
            cornersChanged = true;
        }
    }
    public void setColor(int color) {
        if (this.color != color) {
            this.color = color;
            colorChanged = true;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(corners);
    }

}
