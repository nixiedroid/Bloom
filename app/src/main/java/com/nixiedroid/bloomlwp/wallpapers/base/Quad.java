package com.nixiedroid.bloomlwp.wallpapers.base;

import android.opengl.Matrix;

import java.util.Objects;

public class Quad {
    protected float[] basePositions = new float[18];
    protected float[][] cornerRgbs;
    protected float[] upperLeftRgb;
    protected float[] upperRightRgb;
    protected float[] lowerLeftRgb;
    protected float[] lowerRightRgb;
    protected float[] matrix;
    protected float[] transformedPositions = new float[18];
    float[] textureCoords;

    public Quad(float f, float f2, Origin origin) {
        this.matrix = new float[16];
        this.upperLeftRgb = new float[4];
        this.upperRightRgb = new float[4];
        this.lowerLeftRgb = new float[4];
        this.lowerRightRgb = new float[4];
        this.cornerRgbs = new float[][]{this.upperLeftRgb, this.upperRightRgb, this.lowerLeftRgb, this.lowerRightRgb};
        this.textureCoords = new float[]{
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f};
        Matrix.setIdentityM(this.matrix, 0);
        this.setBasePositions(f, f2, origin);
        this.setColors(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void applyMatrix() {
        float[] fArray = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        float[] result = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        for (int i = 0; i < this.transformedPositions.length; i += 3) {
            fArray[0] = this.basePositions[i];
            fArray[1] = this.basePositions[i + 1];
            fArray[2] = this.basePositions[i + 2];
            Matrix.multiplyMV(result, 0, this.matrix, 0, fArray, 0);
            this.transformedPositions[i] = result[0];
            this.transformedPositions[i + 1] = result[1];
            this.transformedPositions[i + 2] = result[2];
        }
    }

    public void setBasePositions(float x, float y, float z, float d) {
        basePositions[0] = x;
        basePositions[1] = y;
        basePositions[2] = 0.0f;
        basePositions[3] = z;
        basePositions[4] = y;
        basePositions[5] = 0.0f;
        basePositions[6] = z;
        basePositions[7] = d;
        basePositions[8] = 0.0f;
        basePositions[9] = x;
        basePositions[10] = y;
        basePositions[11] = 0.0f;
        basePositions[12] = z;
        basePositions[13] = d;
        basePositions[14] = 0.0f;
        basePositions[15] = x;
        basePositions[16] = d;
        basePositions[17] = 0.0f;
        this.applyMatrix();
    }

    public void setBasePositions(float f, float f2, Origin origin) {
        if (Objects.requireNonNull(origin) == Origin.CENTER) {
            setBasePositions(-f / 2.0f, -f2 / 2.0f, f / 2.0f, f2 / 2.0f);
            return;
        }
        setBasePositions(-f / 2.0f, -f2, f / 2.0f, 0.0f);
    }

    public void setColors(float f, float f2, float f3, float f4) {
        for (float[] fArray : this.cornerRgbs) {
            fArray[0] = f;
            fArray[1] = f2;
            fArray[2] = f3;
            fArray[3] = f4;
        }
    }

    public void setLowerLeftRgb(float x, float y, float z, float d) {
        lowerLeftRgb[0] = x;
        lowerLeftRgb[1] = y;
        lowerLeftRgb[2] = z;
        lowerLeftRgb[3] = d;
    }

    public void setLowerLeftRgb(float[] fArray, float f) {
        this.setLowerLeftRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void setLowerRightRgb(float x, float y, float z, float d) {
        lowerRightRgb[0] = x;
        lowerRightRgb[1] = y;
        lowerRightRgb[2] = z;
        upperRightRgb[3] = d;
    }

    public void setLowerRightRgb(float[] fArray, float f) {
        this.setLowerRightRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void setUpperLeftRgb(float f, float f2, float f3, float f4) {
        upperLeftRgb[0] = f;
        upperLeftRgb[1] = f2;
        upperLeftRgb[2] = f3;
        upperLeftRgb[3] = f4;
    }

    public void setUpperLeftRgb(float[] fArray, float f) {
        this.setUpperLeftRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void setUpperRightRgb(float f, float f2, float f3, float f4) {
        upperRightRgb[0] = f;
        upperRightRgb[1] = f2;
        upperRightRgb[2] = f3;
        upperRightRgb[3] = f4;
    }

    public void setUpperRightRgb(float[] fArray, float f) {
        this.setUpperRightRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void writeColors(float[] fArray, int n) {
        fArray[n] = upperLeftRgb[0];
        fArray[n + 1] = upperLeftRgb[1];
        fArray[n + 2] = upperLeftRgb[2];
        fArray[n + 3] = upperLeftRgb[3];

        fArray[n + 4] = upperRightRgb[0];
        fArray[n + 5] = upperRightRgb[1];
        fArray[n + 6] = upperRightRgb[2];
        fArray[n + 7] = upperRightRgb[3];

        fArray[n + 8] = lowerRightRgb[0];
        fArray[n + 9] = lowerRightRgb[1];
        fArray[n + 10] = lowerRightRgb[2];
        fArray[n + 11] = lowerRightRgb[3];

        fArray[n + 12] = upperLeftRgb[0];
        fArray[n + 13] = upperLeftRgb[1];
        fArray[n + 14] = upperLeftRgb[2];
        fArray[n + 15] = upperLeftRgb[3];

        fArray[n + 16] = lowerRightRgb[0];
        fArray[n + 17] = lowerRightRgb[1];
        fArray[n + 18] = lowerRightRgb[2];
        fArray[n + 19] = lowerRightRgb[3];

        fArray[n + 20] = lowerLeftRgb[0];
        fArray[n + 21] = lowerLeftRgb[1];
        fArray[n + 22] = lowerLeftRgb[2];
        fArray[n + 23] = lowerLeftRgb[3];
    }

    public enum Origin {
        CENTER,
        BOTTOM_CENTER

    }
}

