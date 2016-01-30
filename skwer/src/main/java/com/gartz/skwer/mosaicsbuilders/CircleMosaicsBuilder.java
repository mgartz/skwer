package com.gartz.skwer.mosaicsbuilders;

import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by Martin on 5/3/2014.
 *
 * Draw rosetta like mosaic
 *
 */
public class CircleMosaicsBuilder extends MosaicPathsBuilder {

    @Override
    public ArrayList<Mosaic> buildMosaicPaths(int numMosaics, float width, float height, int mosaicDeformation) {
        ArrayList<Mosaic> mosaics = new ArrayList<>();

        float x0 = width/2;
        float y0 = height/2;

        float radius0 = width*0.05f;
        float radius1 = width*0.08f;
        float radius2 = width*0.18f;
        float radius3 = width*0.20f;
        float radius4 = width*0.32f;
        float radius5 = width*0.34f;
        float radius6 = width*0.47f;
        float radius7 = width*0.49f;
        float radius8 = width*0.8f;

        float deformation = 0.05f * mosaicDeformation * 0;
        float thetaOffset = (float) Math.PI;
        addCircularPaths(mosaics, width, x0, y0, radius7, radius8, 2f, thetaOffset + random.nextFloat()*6, (int) (numMosaics * 3.0), deformation, true, 4);
        addCircularPaths(mosaics, width, x0, y0, radius5, radius6, 2f, thetaOffset + random.nextFloat()*6, (int) (numMosaics * 2.5), deformation, false, 3);
        addCircularPaths(mosaics, width, x0, y0, radius3, radius4, 2f, thetaOffset + random.nextFloat()*6, (int) (numMosaics * 1.8), deformation, false, 2);
        addCircularPaths(mosaics, width, x0, y0, radius1, radius2, 2f, thetaOffset + random.nextFloat() * 6, (int) (numMosaics * 1.5), deformation, false, 1);

        Mosaic centerCircle = new Mosaic();
        centerCircle.addCircle(width/2, height/2, radius0, Path.Direction.CW);
        mosaics.add(centerCircle);

        return mosaics;
    }

    private void addCircularPaths(ArrayList<Mosaic> mosaics, float size, float x0, float y0, float r1, float r2, float thetaGap, float thetaOffset, int numMosaics, float deformation, boolean isLastRow, int index){
        double thetaDelta = Math.PI * 2 / numMosaics;
        double theta = thetaOffset;
        double deformedDelta;
        float adjustedSize = size;
        while (theta <= thetaOffset + Math.PI * 2 - 0.1){
            if (isLastRow)
                adjustedSize = randomizeSize(r1, size, deformation);
            Mosaic path = new Mosaic();
            deformedDelta = Math.min(thetaOffset + Math.PI * 2 - theta, thetaDelta - deformation + random.nextInt(101) / 50.0 * deformation);
            if (theta + deformedDelta > thetaOffset + Math.PI * 2 - 0.1) {
                if (deformedDelta / thetaDelta > 1.4)
                    deformedDelta /= 2;
                else
                    deformedDelta = thetaOffset + Math.PI * 2 - theta;
            }

            double theta11 = theta + thetaGap / r1 + randomSmallAngle(deformation);
            double theta12 = theta + thetaGap / r2 + randomSmallAngle(deformation);
            double theta22 = theta + deformedDelta - thetaGap / r2 + randomSmallAngle(deformation);
            double theta21 = theta + deformedDelta - thetaGap / r1 + randomSmallAngle(deformation);

            float x1 = x0 + x(adjustedSize, r1, theta11);
            float y1 = y0 + y(adjustedSize, r1, theta11);
            float x2 = x0 + x(adjustedSize, r2, theta12);
            float y2 = y0 + y(adjustedSize, r2, theta12);

            if (isLastRow)
                adjustedSize = randomizeSize(r1, size, deformation);

            float x3 = x0 + x(adjustedSize, r2, theta22);
            float y3 = y0 + y(adjustedSize, r2, theta22);
            float x4 = x0 + x(adjustedSize, r1, theta21);
            float y4 = y0 + y(adjustedSize, r1, theta21);

            path.moveTo(x1 + smallTranslation(size), y1 + smallTranslation(size));
            path.lineTo(x2 + smallTranslation(size), y2 + smallTranslation(size));
            if (isLastRow){
                adjustedSize = randomizeSize(r1, size, deformation);

                double th1 = Math.atan2(Math.sin(theta12), Math.cos(theta12));
                double th2 = Math.atan2(Math.sin(theta22), Math.cos(theta22));
                if (th1 < 0)
                    th1 += Math.PI*2;
                if (th2 < 0)
                    th2 += Math.PI*2;
                if (th1 < Math.PI/4 && th2 > Math.PI/4)
                    path.lineTo(adjustedSize,adjustedSize);
                else if (th1 < 3 * Math.PI / 4 && th2 > 3 * Math.PI / 4)
                    path.lineTo(0,adjustedSize);
                else if (th1 < 5 * Math.PI / 4 && th2 > 5 * Math.PI / 4)
                    path.lineTo(0,0);
                else if (th1 < 7 * Math.PI / 4 && th2 > 7 * Math.PI / 4)
                    path.lineTo(adjustedSize,0);
            }
            path.lineTo(x3 + smallTranslation(size), y3 + smallTranslation(size));
            path.lineTo(x4 + smallTranslation(size), y4 + smallTranslation(size));
            path.close();
            mosaics.add(path);
            theta += deformedDelta;
        }
    }

    private float randomizeSize(float minSize, float size, float deformation) {
        deformation = deformation*0.2f;
        return Math.max(minSize,size * (1-deformation + 2*random.nextFloat()*deformation));
    }
    private float randomSmallAngle(float deformation){
        return deformation * (-0.5f + random.nextFloat()) * 0.2f;
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
