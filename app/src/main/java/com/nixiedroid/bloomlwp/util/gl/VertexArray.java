package com.nixiedroid.bloomlwp.util.gl;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] fArray) {
        this.floatBuffer = ByteBuffer.allocateDirect(fArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.floatBuffer.put(fArray);
    }

    public void setVertexAttribPointer(int n, int n2, int n3, int n4) {
        this.floatBuffer.position(n);
        GLES20.glVertexAttribPointer(n2, n3, 5126, false, n4, this.floatBuffer);
        GLES20.glEnableVertexAttribArray(n2);
        this.floatBuffer.position(0);
    }

    public void updateBuffer(float[] fArray, int n, int n2) {
        this.floatBuffer.position(n);
        this.floatBuffer.put(fArray, n, n2);
        this.floatBuffer.position(0);
    }
}

