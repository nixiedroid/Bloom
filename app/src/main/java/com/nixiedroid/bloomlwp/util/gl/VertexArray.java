package com.nixiedroid.bloomlwp.util.gl;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] verticesArray) {
        this.floatBuffer = ByteBuffer
                .allocateDirect(verticesArray.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.floatBuffer.put(verticesArray);
    }

    public void setVertexAttribPointer(int position, int pointerIndex, int size, int stride) {
        this.floatBuffer.position(position);
        GLES20.glVertexAttribPointer(pointerIndex, size, GLES20.GL_FLOAT, false, stride, this.floatBuffer);
        GLES20.glEnableVertexAttribArray(pointerIndex);
        this.floatBuffer.position(0);
    }

    public void updateBuffer(float[] fArray, int position, int value) {
        this.floatBuffer.position(position);
        this.floatBuffer.put(fArray, position, value);
        this.floatBuffer.position(0);
    }
}

