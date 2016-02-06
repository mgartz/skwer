package com.gartz.skwer.mosaic.builder;

import com.gartz.skwer.mosaic.FlippingPolygon;
import com.gartz.skwer.mosaic.Mosaic;
import com.gartz.skwer.mosaic.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gartz on 2/1/16.
 *
 * A mosaic builder for error tiles.
 *
 */
public class ErrorMosaicBuilder extends MosaicBuilder {
    private float minX, maxX, minY, maxY;

    @Override
    public Mosaic buildMosaic(float offsetX, float offsetY) {
        List<Polygon> polygons = new ArrayList<>();

        minX = offsetX - 0.5f;
        maxX = offsetX + 0.5f;
        minY = offsetY - 0.5f;
        maxY = offsetY + 0.5f;

        float mosaicSize = 0.5f  / QUADS_PER_ROW;
        Polygon polygon = new FlippingPolygon();
        addVertex(polygon, offsetX - mosaicSize, offsetY, 1);
        addVertex(polygon, offsetX, offsetY - mosaicSize, 1);
        addVertex(polygon, offsetX + mosaicSize, offsetY, 1);
        addVertex(polygon, offsetX, offsetY + mosaicSize, 1);
        polygons.add(polygon);

        addCrossPaths(polygons, mosaicSize, offsetX, offsetY, -1, -1);
        addCrossPaths(polygons, mosaicSize, offsetX, offsetY, -1,  1);
        addCrossPaths(polygons, mosaicSize, offsetX, offsetY,  1, -1);
        addCrossPaths(polygons, mosaicSize, offsetX, offsetY, 1, 1);

        addPlusPaths(polygons, mosaicSize, offsetX, offsetY, -1, true);
        addPlusPaths(polygons, mosaicSize, offsetX, offsetY,  1, true);
        addPlusPaths(polygons, mosaicSize, offsetX, offsetY, -1, false);
        addPlusPaths(polygons, mosaicSize, offsetX, offsetY,  1, false);

        Mosaic mosaic = new Mosaic();
        mosaic.setPolygons(polygons);
        return mosaic;
    }

    private void addCrossPaths(List<Polygon> polygons, float mosaicSize, float x0, float y0, int dirX, int dirY){
        float r = mosaicSize;
        float gap = 0.015f;
        int numMosaicsInRow = 1;
        x0 -= dirX * (mosaicSize + gap) / 2;
        y0 += dirY * (mosaicSize + gap) / 2;
        while (r < 1/Math.sqrt(2)){
            x0 += dirX * (mosaicSize + gap)* 3 / 2;
            y0 += dirY * (mosaicSize + gap) / 2;
            for (int i=0; i<numMosaicsInRow; i++){
                float xi = x0 - i * (mosaicSize + gap) * dirX;
                float yi = y0 + i * (mosaicSize + gap) * dirY;
                float x1, x2, x3, x4;
                float y1, y2, y3, y4;
                if (i == 0 && numMosaicsInRow > 1){
                    if (dirX < 0 && dirY < 0) {
                        x1 = xi - mosaicSize; y1 = yi;
                        x2 = xi; y2 = yi - mosaicSize;
                        x3 = xi + mosaicSize; y3 = yi;
                        x4 = xi + mosaicSize/2; y4 = yi + mosaicSize - mosaicSize/2;
                    }
                    else if (dirX < 0 && dirY > 0) {
                        x1 = xi - mosaicSize; y1 = yi;
                        x2 = xi + mosaicSize / 2; y2 = yi - mosaicSize + mosaicSize / 2;
                        x3 = xi + mosaicSize; y3 = yi;
                        x4 = xi; y4 = yi + mosaicSize;
                    }
                    else if (dirX > 0 && dirY > 0) {
                        x1 = xi - mosaicSize; y1 = yi;
                        x2 = xi - mosaicSize/2; y2 = yi - mosaicSize + mosaicSize/2;
                        x3 = xi + mosaicSize; y3 = yi;
                        x4 = xi; y4 = yi + mosaicSize;
                    }
                    else {
                        x1 = xi - mosaicSize; y1 = yi;
                        x2 = xi; y2 = yi - mosaicSize;
                        x3 = xi + mosaicSize; y3 = yi;
                        x4 = xi - mosaicSize / 2; y4 = yi + mosaicSize - mosaicSize / 2;
                    }
                }
                else if (i == numMosaicsInRow-1 && numMosaicsInRow > 1){
                    if (dirX < 0 && dirY < 0) {
                        x1 = xi - mosaicSize; y1 = yi;
                        x2 = xi; y2 = yi - mosaicSize;
                        x3 = xi + mosaicSize - mosaicSize / 2; y3 = yi + mosaicSize / 2;
                        x4 = xi; y4 = yi + mosaicSize;
                    }
                    else if (dirX < 0 && dirY > 0) {
                        x1 = xi - mosaicSize; y1 = yi;
                        x2 = xi; y2 = yi - mosaicSize;
                        x3 = xi + mosaicSize - mosaicSize / 2; y3 = yi - mosaicSize / 2;
                        x4 = xi; y4 = yi + mosaicSize;
                    }
                    else if (dirX > 0 && dirY > 0) {
                        x1 = xi - mosaicSize + mosaicSize/2; y1 = yi - mosaicSize / 2;
                        x2 = xi; y2 = yi - mosaicSize;
                        x3 = xi + mosaicSize; y3 = yi;
                        x4 = xi; y4 = yi + mosaicSize;
                    }
                    else {
                        x1 = xi - mosaicSize + mosaicSize / 2; y1 = yi + mosaicSize / 2;
                        x2 = xi; y2 = yi - mosaicSize;
                        x3 = xi + mosaicSize; y3 = yi;
                        x4 = xi; y4 = yi + mosaicSize;
                    }
                }
                else {
                    x1 = xi - mosaicSize; y1 = yi;
                    x2 = xi; y2 = yi - mosaicSize;
                    x3 = xi + mosaicSize; y3 = yi;
                    x4 = xi; y4 = yi + mosaicSize;
                }

                x1 = Math.max(minX, Math.min(maxX, x1));
                x2 = Math.max(minX, Math.min(maxX, x2));
                x3 = Math.max(minX, Math.min(maxX, x3));
                x4 = Math.max(minX, Math.min(maxX, x4));
                y1 = Math.max(minY, Math.min(maxY, y1));
                y2 = Math.max(minY, Math.min(maxY, y2));
                y3 = Math.max(minY, Math.min(maxY, y3));
                y4 = Math.max(minY, Math.min(maxY, y4));
                Polygon polygon = new FlippingPolygon();

                addVertex(polygon, x1, y1, 1);
                addVertex(polygon, x2, y2, 1);
                addVertex(polygon, x3, y3, 1);
                addVertex(polygon, x4, y4, 1);
                polygons.add(polygon);
            }
            r += Math.sqrt(2) * (mosaicSize + gap);
            numMosaicsInRow++;
        }
    }

    private void addPlusPaths(List<Polygon> polygons, float mosaicSize, float x0, float y0, int dir, boolean isHorizontal){
        float d = 1.2f * mosaicSize;
        float oldD = 0;
        float gap = 0.015f;
        float r = mosaicSize + 1.8f*gap;

        while (r < 1) {
            float xi = x0, yi = y0;
            float x1, x2;
            float y1, y2;

            if (isHorizontal) {
                xi += r * dir;
                x1 = xi + d * dir;
                x2 = xi + d * dir;
                y1 = yi - d;
                y2 = yi + d;
            } else {
                yi += r * dir;
                x1 = xi - d;
                x2 = xi + d;
                y1 = yi + d * dir;
                y2 = yi + d * dir;
            }

            addTrianglePath(polygons, xi, yi, x1, y1, x2, y2);

            if (oldD != 0) {
                float gap2 = gap * 0.7f;
                addTrianglePath(
                        polygons,
                        xi + (isHorizontal ? 0 : -oldD + gap * 0.2f), yi + (isHorizontal ? -oldD + gap * 0.2f : 0),
                        xi + (isHorizontal ? 0 : -2 * gap2), yi + (isHorizontal ? -2 * gap2 : 0),
                        x1 + (isHorizontal ? -dir * 4 * gap2 : 2 * gap2), y1 + (isHorizontal ? 2 * gap2 : -dir * 4 * gap2));

                addTrianglePath(
                        polygons,
                        xi + (isHorizontal ? 0 : oldD + gap * 0.2f), yi + (isHorizontal ? oldD + gap * 0.2f : 0),
                        xi + (isHorizontal ? 0 : 2 * gap2), yi + (isHorizontal ? 2 * gap2 : 0),
                        x2 + (isHorizontal ? -dir * 4 * gap2 : -2 * gap2), y2 + (isHorizontal ? -2 * gap2 : -dir * 4 * gap2));

            }

            r += d + gap;
            oldD = d;
            d = (d + gap) * 1.3f;
        }
    }

    private void addTrianglePath(List<Polygon> polygons, float x1, float y1, float x2, float y2, float x3, float y3){
        if (x1 < maxX && x1 > minX && y1 > minY && y1 < maxY) {
            Polygon polygon = new FlippingPolygon();
            addVertex(polygon, x1, y1, 1);

            float interp = 0.1f;
            float adjustedX = x2;
            float adjustedY = y2;
            while (adjustedX > maxX || adjustedX < minX || adjustedY > maxY || adjustedY < minY){
                adjustedX = x2 * (1-interp) + x1 * interp;
                adjustedY = y2 * (1-interp) + y1 * interp;
                interp += 0.1f;
            }
            addVertex(polygon, adjustedX, adjustedY, 1);

            interp = 0.1f;
            adjustedX = x3;
            adjustedY = y3;
            while (adjustedX > maxX || adjustedX < minX || adjustedY > maxY || adjustedY < minY){
                adjustedX = x3 * (1-interp) + x1 * interp;
                adjustedY = y3 * (1-interp) + y1 * interp;
                interp += 0.1f;
            }
            addVertex(polygon, adjustedX, adjustedY, 1);
            polygons.add(polygon);
        }
    }

}
