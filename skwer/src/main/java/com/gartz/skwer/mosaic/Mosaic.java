package com.gartz.skwer.mosaic;

import android.opengl.GLES20;

import com.gartz.skwer.game.GameGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
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

    protected List<Polygon> polygons;

    public FloatBuffer verticesBuffer;
    public FloatBuffer colorsBuffer;
    public ShortBuffer drawListBuffer;

    private int program;

    public void setPolygons(List<Polygon> polygons) {
        this.polygons = polygons;

        short totalVertices = 0;
        short totalTriangles = 0;
        for (Polygon polygon : polygons) {
            short numVertices = polygon.getNumVertices();
            polygon.close(this, totalVertices, totalTriangles);
            totalVertices += numVertices;
            totalTriangles += numVertices - 2;
        }

        float[] vertices = new float[totalVertices * Polygon.COORDS_PER_VERTEX];
        float[] colors = new float[totalVertices * Polygon.CHANNELS_PER_COLOR];
        short[] drawList = new short[totalTriangles * 3];
        setupBuffersAndShaders(vertices, colors, drawList);

        for (Polygon polygon : polygons) {
            polygon.setupDrawIndices();
            polygon.randomizeColorDeltas(60);
            polygon.update();
        }
    }

    private void setupBuffersAndShaders(float[] vertices, float[] colors, short[] drawList) {
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

            int vertexShader = GameGLRenderer.loadShader(
                    GLES20.GL_VERTEX_SHADER,
                    GameGLRenderer.VERTEX_SHADER_CODE);
            int fragmentShader = GameGLRenderer.loadShader(
                    GLES20.GL_FRAGMENT_SHADER,
                    GameGLRenderer.FRAGMENT_SHADER_CODE);

            program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
        }
    }

    public void draw(float[] mvpMatrix) {
        for (Polygon polygon : polygons)
            polygon.update();

        GLES20.glUseProgram(program);
        verticesBuffer.position(0);
        colorsBuffer.position(0);
        drawListBuffer.position(0);

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
                drawListBuffer.remaining(),
                GLES20.GL_UNSIGNED_SHORT,
                drawListBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    public void setColor(int color, float dimFactor) {
        for (Polygon polygon : polygons)
            polygon.setColor(color, dimFactor);
    }

    public void flip(float x0, float y0) {
        for (Polygon polygon : polygons)
            if (polygon instanceof FlippingPolygon) {
                float dist = Math.abs(polygon.center.x - x0) + Math.abs(polygon.center.y - y0);
                ((FlippingPolygon) polygon).flip((int) (dist * 200));
            }
    }

    public void randomizeColorDeltas(int maxRandomDelta) {
        for (Polygon polygon : polygons)
            polygon.randomizeColorDeltas(maxRandomDelta);
    }
}
