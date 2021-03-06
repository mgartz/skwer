package com.gartz.skwer.mosaic;

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

    protected int baseColor;
    private float[] color = new float[4];
    protected float dimFactor = 1;
    protected float colorDelta;
    protected List<PointF> vertices = new ArrayList<>();

    protected boolean verticesChanged = true;
    protected boolean colorChanged = true;
    public PointF center = new PointF();

    public void addVertex(float x, float y) {
        if (!closed)
            vertices.add(new PointF(x, y));
    }

    public void close(Mosaic mosaic, short vertexOffset, short drawListOffset) {
        closed = true;
        calcCenter();
        this.mosaic = mosaic;
        this.vertexOffset = vertexOffset;
        this.drawListOffset = drawListOffset;
    }

    protected void calcCenter() {
        for (PointF vertex : vertices) {
            center.x += vertex.x;
            center.y += vertex.y;
        }
        center.x /= vertices.size();
        center.y /= vertices.size();
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
            PointF vertex;
            for (int i=0; i<vertices.size(); i++) {
                vertex = getVertex(i);
                mosaic.verticesBuffer.put(vertex.x);
                mosaic.verticesBuffer.put(vertex.y);
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
    protected PointF getVertex(int index) {
        return vertices.get(index);
    }
    public void setColor(int color, float dimFactor) {
        if (this.baseColor != color || this.dimFactor != dimFactor) {
            baseColor = color;
            this.dimFactor = dimFactor;
            updateColor();
        }
    }
    public void randomizeColorDeltas(int maxRandomDelta) {
        colorDelta = random.nextInt(2 * maxRandomDelta + 1) - maxRandomDelta;
        updateColor();
    }
    protected void updateColor() {
        this.color[0] = (1-dimFactor) * Math.max(0, Math.min(0xFF, ((baseColor >> 0x10) & 0xFF) + colorDelta)) / 0xFF;
        this.color[1] = (1-dimFactor) * Math.max(0, Math.min(0xFF, ((baseColor >> 0x8) & 0xFF) + colorDelta)) / 0xFF;
        this.color[2] = (1-dimFactor) * Math.max(0, Math.min(0xFF, (baseColor & 0xFF) + colorDelta)) / 0xFF;
        this.color[3] = 1f * ((baseColor >> 0x18) & 0xFF) / 0xFF;
        colorChanged = true;
    }

    @Override
    public String toString() {
        return String.valueOf(vertices);
    }
}
