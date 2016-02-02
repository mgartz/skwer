package com.gartz.skwer;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gartz on 2/1/16.
 *
 * An collection of polygons for opengl rendering.
 *
 */
public class Mosaic {
    private static final int CHANNELS_PER_COLOR = 4;
    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    public float[] vertices;
    public float[] colors;
    public short[] drawList;

    private FloatBuffer verticesBuffer;
    private FloatBuffer colorsBuffer;
    private ShortBuffer drawListBuffer;

    private int program;

    public void setQuads(List<Polygon> polygons) {
        short totalVertices = 0;
        short totalTriangles = 0;
        for (Polygon polygon : polygons) {
            short numVertices = polygon.getNumVertices();
            polygon.close(this, totalVertices, totalTriangles);
            totalVertices += numVertices;
            totalTriangles += numVertices - 2;
        }

        vertices = new float[totalVertices * Polygon.COORDS_PER_VERTEX];
        colors = new float[totalVertices * Polygon.CHANNELS_PER_COLOR];
        drawList = new short[totalTriangles * 3];

        for (Polygon polygon : polygons) {
            polygon.setupDrawIndices();
            polygon.update();
        }
        setupBuffersAndShaders();
    }

    private void setupBuffersAndShaders() {
        if (vertices.length > 0) {
            ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
            vb.order(ByteOrder.nativeOrder());
            verticesBuffer = vb.asFloatBuffer();
            verticesBuffer.put(vertices);
            verticesBuffer.position(0);

            ByteBuffer dlb = ByteBuffer.allocateDirect(drawList.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawList);
            drawListBuffer.position(0);

            ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
            cb.order(ByteOrder.nativeOrder());
            colorsBuffer = cb.asFloatBuffer();
            colorsBuffer.put(colors);
            colorsBuffer.position(0);

            int vertexShader = SkwerGLRenderer.loadShader(
                    GLES20.GL_VERTEX_SHADER,
                    SkwerGLRenderer.VERTEX_SHADER_CODE);
            int fragmentShader = SkwerGLRenderer.loadShader(
                    GLES20.GL_FRAGMENT_SHADER,
                    SkwerGLRenderer.FRAGMENT_SHADER_CODE);

            program = GLES20.glCreateProgram();             // create empty OpenGL Program
            GLES20.glAttachShader(program, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(program, fragmentShader); // add the fragment shader to program
            GLES20.glLinkProgram(program);                  // create OpenGL program executables
        }
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(
                positionHandle,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                VERTEX_STRIDE,
                verticesBuffer);

        int colorHandle = GLES20.glGetAttribLocation(program, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(
                colorHandle,
                CHANNELS_PER_COLOR,
                GLES20.GL_FLOAT,
                false,
                0,
                colorsBuffer);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawList.length,
                GLES20.GL_UNSIGNED_SHORT,
                drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
