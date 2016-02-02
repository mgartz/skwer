package com.gartz.skwer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gartz on 2/1/16.
 *
 * Builds a rosetta style mosaic.
 *
 */
public class RosettaMosaicBuilder extends MosaicBuilder {


    @Override
    public Mosaic buildMosaic(int offsetX, int offsetY) {
        Mosaic mosaic = new Mosaic();
        List<Quad> quads = new ArrayList<>();

        float x0 = offsetX;
        float y0 = offsetY;

        float radius0 = 0.05f;
        float radius1 = 0.08f;
        float radius2 = 0.18f;
        float radius3 = 0.20f;
        float radius4 = 0.32f;
        float radius5 = 0.34f;
        float radius6 = 0.47f;
        float radius7 = 0.49f;
        float radius8 = 0.8f;

        float thetaOffset = (float) Math.PI;
        addCircularPaths(quads, x0, y0, radius7, radius8, 2f, thetaOffset + random.nextFloat()*6, (int) (QUADS_PER_ROW * 3.0), true);
        addCircularPaths(quads, x0, y0, radius5, radius6, 2f, thetaOffset + random.nextFloat()*6, (int) (QUADS_PER_ROW * 2.5), false);
        addCircularPaths(quads, x0, y0, radius3, radius4, 2f, thetaOffset + random.nextFloat()*6, (int) (QUADS_PER_ROW * 1.8), false);
        addCircularPaths(quads, x0, y0, radius1, radius2, 2f, thetaOffset + random.nextFloat() * 6, (int) (QUADS_PER_ROW * 1.5), false);

        addCenterQuad(quads, x0, y0, radius0);

        mosaic.setQuads(quads);
        return mosaic;
    }

    private void addCenterQuad(List<Quad> quads, float x0, float y0, float radius0) {
        Quad quad = new Quad();
        quad.setCorner(0, (float) (x0 + radius0 * Math.sqrt(3) / 2), (float) (y0 + radius0 * Math.sqrt(3) / 2));
        quad.setCorner(1, (float) (x0 - radius0 * Math.sqrt(3) / 2), (float) (y0 + radius0 * Math.sqrt(3) / 2));
        quad.setCorner(2, (float) (x0 + radius0 * Math.sqrt(3) / 2), (float) (y0 - radius0 * Math.sqrt(3) / 2));
        quad.setCorner(3, (float) (x0 - radius0 * Math.sqrt(3) / 2), (float) (y0 - radius0 * Math.sqrt(3) / 2));
        quads.add(quad);
    }

    private void addCircularPaths(List<Quad> quads, float x0, float y0, float r1, float r2, float thetaGap, float thetaOffset, int numMosaics, boolean isLastRow){
        double thetaDelta = Math.PI * 2 / numMosaics;
        double theta = thetaOffset;
        double deformedDelta;
        float adjustedSize = 1;
        while (theta <= thetaOffset + Math.PI * 2 - 0.1) {
            if (isLastRow)
                adjustedSize = clampSize(r1, 1);
            deformedDelta = Math.min(thetaOffset + Math.PI * 2 - theta, thetaDelta);
            if (theta + deformedDelta > thetaOffset + Math.PI * 2 - 0.1) {
                if (deformedDelta / thetaDelta > 1.4)
                    deformedDelta /= 2;
                else
                    deformedDelta = thetaOffset + Math.PI * 2 - theta;
            }

            double theta11 = theta + thetaGap / (r1 * 200);
            double theta12 = theta + thetaGap / (r2 * 200);
            double theta22 = theta + deformedDelta - thetaGap / (r2 * 200);
            double theta21 = theta + deformedDelta - thetaGap / (r1 * 200);

            float x1 = x0 + x(adjustedSize, r1, theta11);
            float y1 = y0 + y(adjustedSize, r1, theta11);
            float x2 = x0 + x(adjustedSize, r2, theta12);
            float y2 = y0 + y(adjustedSize, r2, theta12);

            if (isLastRow)
                adjustedSize = clampSize(r1, 1);

            float x3 = x0 + x(adjustedSize, r2, theta22);
            float y3 = y0 + y(adjustedSize, r2, theta22);
            float x4 = x0 + x(adjustedSize, r1, theta21);
            float y4 = y0 + y(adjustedSize, r1, theta21);

            Quad quad = new Quad();
            quad.setCorner(0, x1 + smallTranslation(), y1 + smallTranslation());
            quad.setCorner(1, x2 + smallTranslation(), y2 + smallTranslation());
//            if (isLastRow) {
//                adjustedSize = randomizeSize(r1, 1, deformation);
//
//                double th1 = Math.atan2(Math.sin(theta12), Math.cos(theta12));
//                double th2 = Math.atan2(Math.sin(theta22), Math.cos(theta22));
//                if (th1 < 0)
//                    th1 += Math.PI * 2;
//                if (th2 < 0)
//                    th2 += Math.PI * 2;
//                if (th1 < Math.PI / 4 && th2 > Math.PI / 4)
//                    path.lineTo(adjustedSize, adjustedSize);
//                else if (th1 < 3 * Math.PI / 4 && th2 > 3 * Math.PI / 4)
//                    path.lineTo(0, adjustedSize);
//                else if (th1 < 5 * Math.PI / 4 && th2 > 5 * Math.PI / 4)
//                    path.lineTo(0, 0);
//                else if (th1 < 7 * Math.PI / 4 && th2 > 7 * Math.PI / 4)
//                    path.lineTo(adjustedSize, 0);
//            }
            quad.setCorner(3, x3 + smallTranslation(), y3 + smallTranslation());
            quad.setCorner(2, x4 + smallTranslation(), y4 + smallTranslation());
            quads.add(quad);
            theta += deformedDelta;
        }
    }

    private float clampSize(float minSize, float size) {
        return Math.max(minSize,size * 1);
    }

    private float x(float width, float r, double theta){
        double rr = Math.min(r, maxR(width/2, theta));
        return (float) (rr * Math.cos(theta));
    }
    private float y(float width, float r, double theta){
        double rr = Math.min(r, maxR(width/2, theta));
        return (float) (rr * Math.sin(theta));
    }
    private double maxR(float r, double theta){
        double th = Math.atan2(Math.sin(theta), Math.cos(theta));
        if (th < 0)
            th += Math.PI * 2;

        double arg;
        if (th < Math.PI / 4)
            arg = th;
        else if (th < Math.PI / 2)
            arg = Math.PI/2 - th;
        else if (th < 3 * Math.PI / 4)
            arg = th - Math.PI/2;
        else if (th < Math.PI)
            arg = Math.PI - th;
        else if (th < 5 * Math.PI / 4)
            arg = th - Math.PI;
        else if (th < 3 * Math.PI / 2)
            arg = Math.PI*3/2 - th;
        else if (th < 7 * Math.PI / 4)
            arg = th - 3*Math.PI/2;
        else
            arg = Math.PI*2 - th;

        return r / Math.cos(arg);
    }
}
