package com.gartz.skwer;

import java.util.ArrayList;

/**
 * Created by gartz on 2/1/16.
 *
 * Builds a grid mosaic.
 *
 */
public class GridMosaicBuilder extends MosaicBuilder{

    @Override
    public Mosaic buildMosaic(int offsetX, int offsetY) {
        ArrayList<Quad> quads = new ArrayList<>();
        float[][][] vertices;

        float wn = 1f / QUADS_PER_ROW;
        float hn = 1f / QUADS_PER_ROW;
        float strokeSize = 0.01f;

        vertices = new float[QUADS_PER_ROW + 1][QUADS_PER_ROW + 1][2];

        for (int i=0; i<QUADS_PER_ROW + 1; i++)
            for (int j=0; j<QUADS_PER_ROW + 1; j++){
                float deformationX = 2f * DEFORMATION / QUADS_PER_ROW;
                float deformationY = 2f * DEFORMATION / QUADS_PER_ROW;
                if (i == 0 || i == QUADS_PER_ROW)
                    deformationX = deformationX * 0.2f;
                if (j == 0 || j == QUADS_PER_ROW)
                    deformationY = deformationY * 0.2f;
                vertices[i][j][0] = offsetX - 0.5f + i*wn + mediumTranslation(deformationX);
                vertices[i][j][1] = offsetY - 0.5f + j*hn + mediumTranslation(deformationY);
            }
        for (int i=0; i<QUADS_PER_ROW; i++)
            for (int j=0; j<QUADS_PER_ROW; j++){
                Quad quad = new Quad();
                quad.setCorner(0, vertices[i    ][j + 1][0] + strokeSize + smallTranslation(), vertices[i    ][j + 1][1] - strokeSize + smallTranslation());
                quad.setCorner(1, vertices[i    ][j    ][0] + strokeSize + smallTranslation(), vertices[i    ][j    ][1] + strokeSize + smallTranslation());
                quad.setCorner(2, vertices[i + 1][j + 1][0] - strokeSize + smallTranslation(), vertices[i + 1][j + 1][1] - strokeSize + smallTranslation());
                quad.setCorner(3, vertices[i + 1][j    ][0] - strokeSize + smallTranslation(), vertices[i + 1][j    ][1] + strokeSize + smallTranslation());
                quads.add(quad);
            }

        Mosaic mosaic = new Mosaic();
        mosaic.setQuads(quads);
        return mosaic;
    }

    private float mediumTranslation(float deformation){
        return (-0.5f + random.nextFloat()) * deformation * 0.15f;
    }

}
