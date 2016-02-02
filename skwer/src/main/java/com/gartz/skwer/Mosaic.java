package com.gartz.skwer;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 * Created by gartz on 2/1/16.
 *
 * An collection of quadrilaterals and circles for opengl rendering.
 *
 */
public class Mosaic {
    private static final int CHANNELS_PER_COLOR = 4;
    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    public float[] quadVertices;
    public float[] quadColors;
    private short[] quadDrawList;

    private FloatBuffer quadVerticesBuffer;
    private FloatBuffer quadColorsBuffer;
    private ShortBuffer quadDrawListBuffer;

    private int quadProgram;

    public void setQuads(List<Quad> quads) {
        quadVertices = new float[Quad.VERTICES_PER_QUAD * Quad.COORDS_PER_VERTEX * quads.size()];
        quadColors = new float[Quad.COLORS_PER_QUAD * Quad.CHANNELS_PER_COLOR * quads.size()];
        quadDrawList = new short[6 * quads.size()];
        for (short i=0; i<quads.size(); i++) {
            Quad quad = quads.get(i);
            quad.mosaic = this;
            quad.offset = i;
            quad.update();

            quadDrawList[i * 6] = (short) (i*4);
            quadDrawList[i * 6 + 1] = (short) (i*4 + 1);
            quadDrawList[i * 6 + 2] = (short) (i*4 + 2);
            quadDrawList[i * 6 + 3] = (short) (i*4 + 2);
            quadDrawList[i * 6 + 4] = (short) (i*4 + 1);
            quadDrawList[i * 6 + 5] = (short) (i*4 + 3);
        }
        setupBuffersAndShaders();
    }

    private void setupBuffersAndShaders() {
        if (quadVertices.length > 0) {
            ByteBuffer vb = ByteBuffer.allocateDirect(quadVertices.length * 4);
            vb.order(ByteOrder.nativeOrder());
            quadVerticesBuffer = vb.asFloatBuffer();
            quadVerticesBuffer.put(quadVertices);
            quadVerticesBuffer.position(0);

            ByteBuffer dlb = ByteBuffer.allocateDirect(quadDrawList.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            quadDrawListBuffer = dlb.asShortBuffer();
            quadDrawListBuffer.put(quadDrawList);
            quadDrawListBuffer.position(0);

            ByteBuffer cb = ByteBuffer.allocateDirect(quadColors.length * 4);
            cb.order(ByteOrder.nativeOrder());
            quadColorsBuffer = cb.asFloatBuffer();
            quadColorsBuffer.put(quadColors);
            quadColorsBuffer.position(0);

            int vertexShader = SkwerGLRenderer.loadShader(
                    GLES20.GL_VERTEX_SHADER,
                    SkwerGLRenderer.VERTEX_SHADER_CODE);
            int fragmentShader = SkwerGLRenderer.loadShader(
                    GLES20.GL_FRAGMENT_SHADER,
                    SkwerGLRenderer.FRAGMENT_SHADER_CODE);

            quadProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
            GLES20.glAttachShader(quadProgram, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(quadProgram, fragmentShader); // add the fragment shader to program
            GLES20.glLinkProgram(quadProgram);                  // create OpenGL program executables
        }
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(quadProgram);

        int positionHandle = GLES20.glGetAttribLocation(quadProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                quadVerticesBuffer);

        int colorHandle = GLES20.glGetAttribLocation(quadProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(
                colorHandle,
                CHANNELS_PER_COLOR,
                GLES20.GL_FLOAT,
                false,
                0,
                quadColorsBuffer);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(quadProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                quadDrawList.length,
                GLES20.GL_UNSIGNED_SHORT,
                quadDrawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
