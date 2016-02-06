package com.gartz.skwer.mosaic;

import android.graphics.PointF;

/**
 * Created by gartz on 2/5/16.
 *
 * An extension of polygon that manages transition between two different polygons.
 *
 */
public class TransitionPolygon extends Polygon {
    public static final int TRANSITION_DURATION = 300;

    Polygon origin;
    Polygon target;

    long transitionStartTime;
    float transition;

    int numVerticesOrigin;
    int numVerticesTarget;

    float originColorDelta;
    float targetColorDelta;

    int originRotation;
    int targetRotation;

    PointF point = new PointF();

    public TransitionPolygon(Polygon origin, Polygon target) {
        this.origin = origin;
        this.target = target;

        if (origin == null && target == null)
            return;

        numVerticesOrigin = origin != null ? origin.getNumVertices() : 0;
        numVerticesTarget = target != null ? target.getNumVertices() : 0;

        originColorDelta = origin != null ? origin.colorDelta : 0;
        targetColorDelta = target != null ? target.colorDelta : 0;

        int numVertices = Math.max(numVerticesOrigin, numVerticesTarget);
        for (int i=0; i<numVertices; i++)
            addVertex(0, 0);

        findBestRotation();
    }

    private void findBestRotation() {
        if (origin != null && target != null) {
            float minDist = Float.MAX_VALUE;
            int bestRotation = 0;
            boolean searchingForTargetRotation = numVerticesTarget < numVerticesOrigin;
            for (int i=0; i<numVerticesTarget; i++) {
                float dist = squaredDistForRotation(searchingForTargetRotation ? origin : target, searchingForTargetRotation ? target : origin, i);
                if (dist < minDist) {
                    bestRotation = i;
                    minDist = dist;
                }
            }
            if (searchingForTargetRotation)
                targetRotation = bestRotation;
            else
                originRotation = bestRotation;
        }
    }
    private float squaredDistForRotation(Polygon polygon, Polygon rotated, int rotation) {
        short numVerticesRotated = rotated.getNumVertices();
        int lastI = 0;
        float dist = 0;
        for (int i = 0; i < polygon.getNumVertices(); i++) {
            if (i < numVerticesRotated)
                lastI = i;
            int rotatedIndex = (lastI + rotation) % numVerticesRotated;
            PointF a = polygon.getVertex(i);
            PointF b = rotated.getVertex(rotatedIndex);
            dist += Math.pow((a.x - polygon.center.x) - (b.x - rotated.center.x), 2) + Math.pow((a.y - center.y) - (b.y - center.y), 2);
        }
        return dist;
    }


    @Override
    protected void calcCenter() {
        center.x = target != null ? target.center.x : origin.center.x;
        center.y = target != null ? target.center.y : origin.center.y;
    }

    public void setTransitionStartTime(long transitionStartTime) {
        this.transitionStartTime = transitionStartTime;
        transition = 0;
        interpolateVerticesAndColor(transition);
        verticesChanged = true;
    }

    @Override
    public void update() {
        super.update();

        if (transition < 1) {
            transition = 1f * (System.currentTimeMillis() - transitionStartTime) / TRANSITION_DURATION;
            if (transition > 1)
                transition = 1;
            interpolateVerticesAndColor(transition);
            verticesChanged = true;
        }
    }

    private void interpolateVerticesAndColor(float t) {
        point.x = center.x;
        point.y = center.y;
        for (int i=0; i<vertices.size(); i++) {
            PointF origPoint = i < numVerticesOrigin ? origin.getVertex((i + originRotation) % numVerticesOrigin) : point;
            PointF targetPoint = i < numVerticesTarget ? target.getVertex((i + targetRotation) % numVerticesTarget) : point;
            if (numVerticesOrigin == i - 1) {
                point.x = origPoint.x;
                point.y = origPoint.y;
            }
            if (numVerticesTarget == i - 1) {
                point.x = targetPoint.x;
                point.y = targetPoint.y;
            }

            vertices.get(i).x = origPoint.x * (1-t) + targetPoint.x * t;
            vertices.get(i).y = origPoint.y * (1-t) + targetPoint.y * t;
        }
        colorDelta = originColorDelta * (1-t) + targetColorDelta * t;
        updateColor();
    }
}
