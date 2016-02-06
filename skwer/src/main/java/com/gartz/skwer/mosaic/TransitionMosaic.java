package com.gartz.skwer.mosaic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gartz on 2/5/16.
 *
 * A mosaic to be used to transition between mosaics.
 *
 */
public class TransitionMosaic extends Mosaic {
    public static final int TRANSITION_DURATION = 300;

    private Mosaic origin;
    private Mosaic target;

    private long transitionStartTime;

    public void prepareMatches(Mosaic origin, Mosaic target) {
        this.origin = origin;
        this.target = target;

        if (polygons == null)
            polygons = new ArrayList<>();

        findMatches();
    }

    private void findMatches() {
        boolean targetIsSmaller = origin.polygons.size() > target.polygons.size();
        List<Polygon> availablePolys = new ArrayList<>(targetIsSmaller ? origin.polygons : target.polygons);

        for (Polygon polygon : targetIsSmaller ? target.polygons : origin.polygons) {
            Polygon other = pullClosestPolygon(availablePolys, polygon);
            if (targetIsSmaller)
                polygons.add(new TransitionPolygon(other, polygon));
            else
                polygons.add(new TransitionPolygon(polygon, other));
        }
        while (availablePolys.size() > 0) {
            Polygon other = availablePolys.get(0);
            availablePolys.remove(0);
            if (targetIsSmaller)
                polygons.add(new TransitionPolygon(other, null));
            else
                polygons.add(new TransitionPolygon(null, other));
        }
        setPolygons(polygons);
    }

    private Polygon pullClosestPolygon(List<Polygon> list, Polygon reference) {
        double minDist = Float.MAX_VALUE;
        Polygon minDistPolygon = null;
        int minDistIndex = -1;

        int i=0;
        for (Polygon polygon : list) {
            double dist = squareDistBetweenPolys(polygon, reference);
            if (dist < minDist) {
                minDist = dist;
                minDistPolygon = polygon;
                minDistIndex = i;
            }
            i++;
        }
        if (minDistIndex >= 0)
            list.remove(minDistIndex);
        return minDistPolygon;
    }
    private double squareDistBetweenPolys(Polygon a, Polygon b) {
        return Math.pow(a.center.x - b.center.x, 2) + Math.pow(a.center.y - b.center.y, 2);
    }

    public void startTransition() {
        transitionStartTime = System.currentTimeMillis();
        for (Polygon polygon : polygons)
            ((TransitionPolygon)polygon).setTransitionStartTime(transitionStartTime);
    }

    public boolean isTransitionDone() {
        return System.currentTimeMillis() - transitionStartTime > TRANSITION_DURATION;
    }

    public Mosaic getTarget() {
        return target;
    }
}
