package com.gartz.skwer;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gartz on 2/1/16.
 *
 * A mosaic element.
 *
 */
public class Polygon {
    public static byte COORDS_PER_VERTEX = 3;
    public static byte CHANNELS_PER_COLOR = 4;

    static Random random = new Random();

    Mosaic mosaic;
    short vertexOffset;
    short drawListOffset;

    private boolean closed;

    private int baseColor;
    private float[] color = new float[4];
    private List<PointF> vertices = new ArrayList<>();

    private boolean verticesChanged = true;
    private boolean colorChanged = true;

    public void addVertex(float x, float y) {
        if (!closed)
            vertices.add(new PointF(x, y));
    }

    public void close(Mosaic mosaic, short vertexOffset, short drawListOffset) {
        closed = true;
        this.mosaic = mosaic;
        this.vertexOffset = vertexOffset;
        this.drawListOffset = drawListOffset;
    }

    public short getNumVertices() {
        return (short) vertices.size();
    }

    public void setupDrawIndices() {
        mosaic.drawListBuffer.position(drawListOffset * 3);
        for (int i=0; i<vertices.size()-2; i++) {
            mosaic.drawListBuffer.put(vertexOffset);
            mosaic.drawListBuffer.put((short) (vertexOffset + 1 + i));
            mosaic.drawListBuffer.put((short) (vertexOffset + 2 + i));
        }
    }

    public void update() {
        if (verticesChanged) {
            mosaic.verticesBuffer.position(vertexOffset * COORDS_PER_VERTEX);
            for (int i=0; i<vertices.size(); i++) {
                mosaic.verticesBuffer.put(vertices.get(i).x);
                mosaic.verticesBuffer.put(vertices.get(i).y);
                mosaic.verticesBuffer.put(0);
            }
            verticesChanged = false;
        }
        if (colorChanged) {
            mosaic.colorsBuffer.position(vertexOffset * CHANNELS_PER_COLOR);
            for (int i=0; i< vertices.size(); i++)
                mosaic.colorsBuffer.put(color);
            colorChanged = false;
        }
    }
    public void setVertex(int index, float x, float y) {
        if (vertices.get(index).x != x || vertices.get(index).y != y) {
            vertices.get(index).x = x;
            vertices.get(index).y = y;
            verticesChanged = true;
        }
    }
    public void setColor(int color, int maxRandomDelta) {
        if (this.baseColor != color) {
            baseColor = color;
            int randomDelta = random.nextInt(2 * maxRandomDelta + 1) - maxRandomDelta;

            this.color[0] = 1f * Math.max(0, Math.min(0xFF, ((baseColor >> 0x10) & 0xFF) + randomDelta)) / 0xFF;
            this.color[1] = 1f * Math.max(0, Math.min(0xFF, ((baseColor >> 0x8) & 0xFF) + randomDelta)) / 0xFF;
            this.color[2] = 1f * Math.max(0, Math.min(0xFF, (baseColor & 0xFF) + randomDelta)) / 0xFF;
            this.color[3] = 1f * ((color >> 0x18) & 0xFF) / 0xFF;
            colorChanged = true;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(vertices);
    }
}
