package com.gartz.skwer.mosaicsbuilders;

import java.util.ArrayList;

/**
 * Created by Martin on 5/3/2014.
 *
 * Creates mosaic paths for a deformed grid style.
 *
 */
public class GridMosaicsBuilder extends MosaicPathsBuilder {

    @Override
    public ArrayList<Mosaic> buildMosaicPaths(int numMosaics, float width, float height, int mosaicDeformation) {
        ArrayList<Mosaic> mosaics = new ArrayList<>();
        float[][][] vertices;

        float wn = width /numMosaics;
        float hn = height /numMosaics;

        float strokeSize = width / 100f;

        vertices = new float[numMosaics +1][numMosaics +1][2];

        for (int i=0; i<numMosaics +1; i++)
            for (int j=0; j<numMosaics +1; j++){
                float deformationX = 2f*mosaicDeformation/numMosaics;
                float deformationY = 2f*mosaicDeformation/numMosaics;
                if (i == 0 || i == numMosaics)
                    deformationX = deformationX * 0.2f;
                if (j == 0 || j == numMosaics)
                    deformationY = deformationY * 0.2f;
                vertices[i][j][0] = i*wn + mediumTranslation(width, deformationX);
                vertices[i][j][1] = j*hn + mediumTranslation(width, deformationY);
            }
        for (int i=0; i<numMosaics; i++)
            for (int j=0; j<numMosaics; j++){
                Mosaic path = new Mosaic();
                path.moveTo(vertices[i][j][0] + strokeSize + smallTranslation(width), vertices[i][j][1] + strokeSize + smallTranslation(width));
                path.lineTo(vertices[i][j+1][0] + strokeSize + smallTranslation(width), vertices[i][j+1][1] - strokeSize + smallTranslation(width));
                path.lineTo(vertices[i+1][j+1][0] - strokeSize + smallTranslation(width), vertices[i+1][j+1][1] - strokeSize + smallTranslation(width));
                path.lineTo(vertices[i+1][j][0] - strokeSize + smallTranslation(width), vertices[i+1][j][1] + strokeSize + smallTranslation(width));
                path.close();
                mosaics.add(path);
            }
        return mosaics;
    }

    private float mediumTranslation(float size, float deformation){
        return (-0.5f + random.nextFloat()) * deformation * 0.15f * size;
    }

}
