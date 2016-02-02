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

    Mosaic mosaic;
    short vertexOffset;
    short drawListOffset;

    private boolean closed;

    private int color;
    private List<PointF> vertices = new ArrayList<>();

    private boolean verticesChanged = true;
    private boolean colorChanged = true;

    public Polygon() {
        color = 0xFFFF0080;
    }

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
        for (int i=0; i<vertices.size()-2; i++) {
            mosaic.drawList[(drawListOffset + i) * 3] = vertexOffset;
            mosaic.drawList[(drawListOffset + i) * 3 + 1] = (short) (vertexOffset + 1 + i);
            mosaic.drawList[(drawListOffset + i) * 3 + 2] = (short) (vertexOffset + 2 + i);
        }
    }

    public void update() {
        if (verticesChanged) {
            for (int i=0; i<vertices.size(); i++) {
                mosaic.vertices[(vertexOffset + i) * COORDS_PER_VERTEX] = vertices.get(i).x;
                mosaic.vertices[(vertexOffset + i) * COORDS_PER_VERTEX + 1] = vertices.get(i).y;
                mosaic.vertices[(vertexOffset + i) * COORDS_PER_VERTEX + 2] = 0;
            }
            verticesChanged = false;
        }
        if (colorChanged) {
            float[] c = {
                    1f * ((color >> 0x10) & 0xFF) / 0xFF,
                    1f * ((color >> 0x8) & 0xFF) / 0xFF,
                    1f * (color & 0xFF) / 0xFF,
                    1f * ((color >> 0x18) & 0xFF) / 0xFF
            };
            for (int i=0; i< vertices.size(); i++)
                for (int j=0; j<CHANNELS_PER_COLOR; j++)
                    mosaic.colors[vertexOffset * CHANNELS_PER_COLOR + i * CHANNELS_PER_COLOR + j] = c[j];
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
    public void setColor(int color) {
        if (this.color != color) {
            this.color = color;
            colorChanged = true;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(vertices);
    }
}
