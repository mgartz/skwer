package com.gartz.skwer.mosaic;

import android.graphics.PointF;

import java.util.Random;

/**
 * Created by gartz on 2/3/16.
 *
 * A class in charge of wrapping the flip logic for a polygon.
 *
 */
public class FlippingPolygon extends Polygon {
    public static final long FLIP_DURATION = 150;
    public static final int MAX_DELAY = 50;

    private static Random random = new Random();

    private long flipStartTime;
    private float currentFlipFactor = 0;

    private PointF point = new PointF();

    public void flip(int delay) {
        currentFlipFactor = 0.0001f;
        flipStartTime = System.currentTimeMillis() + delay + random.nextInt(MAX_DELAY);
    }

    @Override
    public void update() {
        super.update();

        if (currentFlipFactor > 0) {
            if (System.currentTimeMillis() > flipStartTime) {
                currentFlipFactor = 1f * (System.currentTimeMillis() - flipStartTime) / FLIP_DURATION;
                if (currentFlipFactor > 1)
                    currentFlipFactor = 0;
            }
            verticesChanged = true;
        }
    }

    @Override
    protected PointF getVertex(int index) {
        if (currentFlipFactor == 0)
            return super.getVertex(index);
        else {
            int otherIndex = index + 1;
            if (vertices.size() == otherIndex)
                otherIndex = 0;
            point.x = vertices.get(otherIndex).x * (1 - currentFlipFactor) + vertices.get(index).x * (currentFlipFactor);
            point.y = vertices.get(otherIndex).y * (1 - currentFlipFactor) + vertices.get(index).y * (currentFlipFactor);
            return point;
        }
    }

}
