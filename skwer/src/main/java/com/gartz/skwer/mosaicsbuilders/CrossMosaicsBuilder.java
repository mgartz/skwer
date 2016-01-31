package com.gartz.skwer.mosaicsbuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin on 5/3/2014.
 *
 * A Cross patter used to show a mistake.
 *
 */
public class CrossMosaicsBuilder extends MosaicPathsBuilder{
    private float minX, maxX, minY, maxY;

    @Override
    public List<Mosaic> buildMosaicPaths(int numMosaics, float width, float height, int mosaicDeformation) {
        ArrayList<Mosaic> mosaics = new ArrayList<>();

        float x0 = width/2;
        float y0 = height/2;

        minX = x0 - width/2;
        maxX = x0 + width/2;
        minY = y0 - width/2;
        maxY = y0 + width/2;

        float mosaicSize = width * 0.5f  / numMosaics;
        Mosaic centerRect = new Mosaic();
        centerRect.moveTo(x0 - mosaicSize, y0);
        centerRect.lineTo(x0, y0 - mosaicSize);
        centerRect.lineTo(x0 + mosaicSize, y0);
        centerRect.lineTo(x0, y0 + mosaicSize);
        centerRect.close();
        mosaics.add(centerRect);

        float deformation = 1 + (0.4f * mosaicDeformation);

        addCrossPaths(mosaics, width, mosaicSize, x0, y0, -1, -1, deformation, 0);
        addCrossPaths(mosaics, width, mosaicSize, x0, y0, -1,  1, deformation, 0);
        addCrossPaths(mosaics, width, mosaicSize, x0, y0,  1, -1, deformation, 0);
        addCrossPaths(mosaics, width, mosaicSize, x0, y0,  1,  1, deformation, 0);

        addPlusPaths(mosaics, width, mosaicSize, x0, y0, -1, true, deformation, 0);
        addPlusPaths(mosaics, width, mosaicSize, x0, y0,  1, true, deformation, 0);
        addPlusPaths(mosaics, width, mosaicSize, x0, y0, -1, false, deformation, 0);
        addPlusPaths(mosaics, width, mosaicSize, x0, y0,  1, false, deformation, 0);

        return mosaics;
    }

    private void addCrossPaths(ArrayList<Mosaic> mosaics, float size, float mosaicSize, float x0, float y0, int dirX, int dirY, float deformation, int colorDeltaOffset){
        float r = mosaicSize;
        float gap = size * 0.015f;
        int numMosaicsInRow = 1;
        x0 -= dirX * (mosaicSize + gap) / 2;
        y0 += dirY * (mosaicSize + gap) / 2;
        while (r < size/Math.sqrt(2)){
            x0 += dirX * (mosaicSize + gap)* 3 / 2;
            y0 += dirY * (mosaicSize + gap) / 2;
            for (int i=0; i<numMosaicsInRow; i++){
                Mosaic path = new Mosaic();
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
                path.moveTo(x1 + smallTranslation(size * deformation),y1 + smallTranslation(size * deformation));
                path.lineTo(x2 + smallTranslation(size * deformation), y2 + smallTranslation(size * deformation));
                path.lineTo(x3 + smallTranslation(size * deformation), y3 + smallTranslation(size * deformation));
                path.lineTo(x4 + smallTranslation(size * deformation),y4 + smallTranslation(size * deformation));
                path.close();
                path.deltaOffset = colorDeltaOffset;
                mosaics.add(path);
            }
            r += Math.sqrt(2) * (mosaicSize + gap);
            numMosaicsInRow++;
        }
    }

    private void addPlusPaths(ArrayList<Mosaic> mosaics, float size, float mosaicSize, float x0, float y0, int dir, boolean isHorizontal, float deformation, int colorDeltaOffset){
        float d = 1.2f * mosaicSize;
        float oldD = 0;
        float gap = 0.015f * size;
        float r = mosaicSize + 1.8f*gap;

        while (r < size) {
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

            addTrianglePath(mosaics, size, xi, yi, x1, y1, x2, y2, deformation, colorDeltaOffset);

            if (oldD != 0) {
                float gap2 = gap * 0.7f;
                addTrianglePath(mosaics,
                        size,
                        xi + (isHorizontal ? 0 : -oldD + gap * 0.2f), yi + (isHorizontal ? -oldD + gap * 0.2f : 0),
                        xi + (isHorizontal ? 0 : -2 * gap2), yi + (isHorizontal ? -2 * gap2 : 0),
                        x1 + (isHorizontal ? -dir * 4 * gap2 : 2 * gap2), y1 + (isHorizontal ? 2 * gap2 : -dir * 4 * gap2),
                        deformation,
                        colorDeltaOffset);

                addTrianglePath(mosaics,
                        size,
                        xi + (isHorizontal ? 0 : oldD + gap * 0.2f), yi + (isHorizontal ? oldD + gap * 0.2f : 0),
                        xi + (isHorizontal ? 0 : 2 * gap2), yi + (isHorizontal ? 2 * gap2 : 0),
                        x2 + (isHorizontal ? -dir * 4 * gap2 : -2 * gap2), y2 + (isHorizontal ? -2 * gap2 : -dir * 4 * gap2),
                        deformation,
                        colorDeltaOffset);

            }

            r += d + gap;
            oldD = d;
            d = (d + gap) * 1.3f;
        }
    }

    private void addTrianglePath(ArrayList<Mosaic> mosaics, float size, float x1, float y1, float x2, float y2, float x3, float y3, float deformation, int colorDeltaOffset){
        if (x1 < maxX && x1 > minX && y1 > minY && y1 < maxY) {
            Mosaic path = new Mosaic();
            path.moveTo(x1 + smallTranslation(size * deformation), y1 + smallTranslation(size * deformation));

            float interp = 0.1f;
            float adjustedX = x2;
            float adjustedY = y2;
            while (adjustedX > maxX || adjustedX < minX || adjustedY > maxY || adjustedY < minY){
                adjustedX = x2 * (1-interp) + x1 * interp;
                adjustedY = y2 * (1-interp) + y1 * interp;
                interp += 0.1f;
            }
            path.lineTo(adjustedX + smallTranslation(size * deformation), adjustedY + smallTranslation(size * deformation));

            interp = 0.1f;
            adjustedX = x3;
            adjustedY = y3;
            while (adjustedX > maxX || adjustedX < minX || adjustedY > maxY || adjustedY < minY){
                adjustedX = x3 * (1-interp) + x1 * interp;
                adjustedY = y3 * (1-interp) + y1 * interp;
                interp += 0.1f;
            }
            path.lineTo(adjustedX + smallTranslation(size * deformation), adjustedY + smallTranslation(size * deformation));
            path.close();

            path.deltaOffset = colorDeltaOffset;
            mosaics.add(path);
        }
    }


}
