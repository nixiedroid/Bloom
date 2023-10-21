package com.nixiedroid.bloomlwp.wallpapers.base;

import android.opengl.Matrix;

import java.util.Objects;

public class Quad {
    protected float[] basePositions = new float[18];
    protected float[][] cornerRgbs;
    protected float[] lowerLeftRgb;
    protected float[] lowerRightRgb;
    protected float[] matrix;
    float[] textureCoords;
    protected float[] transformedPositions = new float[18];
    protected float[] upperLeftRgb;
    protected float[] upperRightRgb;

    public Quad(float f, float f2, Origin origin) {
        this.matrix = new float[16];
        this.upperLeftRgb = new float[4];
        this.upperRightRgb = new float[4];
        this.lowerLeftRgb = new float[4];
        this.lowerRightRgb = new float[4];
        this.cornerRgbs = new float[][]{this.upperLeftRgb, this.upperRightRgb, this.lowerLeftRgb, this.lowerRightRgb};
        this.textureCoords = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        Matrix.setIdentityM(this.matrix, 0);
        this.setBasePositions(f, f2, origin);
        this.setColors(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void applyMatrix() {
        float[] fArray = new float[4];
        fArray[3] = 1.0f;
        float[] fArray2 = new float[4];
        fArray2[3] = 1.0f;
        for (int i = 0; i < this.transformedPositions.length; i += 3) {
            float[] fArray3 = this.basePositions;
            fArray[0] = fArray3[i];
            int n = i + 1;
            fArray[1] = fArray3[n];
            int n2 = i + 2;
            fArray[2] = fArray3[n2];
            Matrix.multiplyMV(fArray2, 0, this.matrix, 0, fArray, 0);
            fArray3 = this.transformedPositions;
            fArray3[i] = fArray2[0];
            fArray3[n] = fArray2[1];
            fArray3[n2] = fArray2[2];
        }
    }

    public void setBasePositions(float f, float f2, float f3, float f4) {
        float[] fArray = this.basePositions;
        fArray[0] = f;
        fArray[1] = f2;
        fArray[2] = 0.0f;
        fArray[3] = f3;
        fArray[4] = f2;
        fArray[5] = 0.0f;
        fArray[6] = f3;
        fArray[7] = f4;
        fArray[8] = 0.0f;
        fArray[9] = f;
        fArray[10] = f2;
        fArray[11] = 0.0f;
        fArray[12] = f3;
        fArray[13] = f4;
        fArray[14] = 0.0f;
        fArray[15] = f;
        fArray[16] = f4;
        fArray[17] = 0.0f;
        this.applyMatrix();
    }

    public void setBasePositions(float f, float f2, Origin origin) {
        float f3;
        float f4;
        //Switch these down if fails
        if (Objects.requireNonNull(origin) == Origin.CENTER) {
            float f5 = -f / 2.0f;
            f4 = -f2 / 2.0f;
            f3 = f2 / 2.0f;
            f2 = f /= 2.0f;
            f = f5;
        } else {
            f3 = -f / 2.0f;
            f4 = -f2;
            f2 = f / 2.0f;
            float f6 = 0.0f;
            f = f3;
            f3 = f6;
        }
        this.setBasePositions(f, f4, f2, f3);
    }

    public void setColors(float f, float f2, float f3, float f4) {
        for (float[] fArray : this.cornerRgbs) {
            fArray[0] = f;
            fArray[1] = f2;
            fArray[2] = f3;
            fArray[3] = f4;
        }
    }

    public void setLowerLeftRgb(float f, float f2, float f3, float f4) {
        float[] fArray = this.lowerLeftRgb;
        fArray[0] = f;
        fArray[1] = f2;
        fArray[2] = f3;
        fArray[3] = f4;
    }

    public void setLowerLeftRgb(float[] fArray, float f) {
        this.setLowerLeftRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void setLowerRightRgb(float f, float f2, float f3, float f4) {
        float[] fArray = this.lowerRightRgb;
        fArray[0] = f;
        fArray[1] = f2;
        fArray[2] = f3;
        this.upperRightRgb[3] = f4;
    }

    public void setLowerRightRgb(float[] fArray, float f) {
        this.setLowerRightRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void setUpperLeftRgb(float f, float f2, float f3, float f4) {
        float[] fArray = this.upperLeftRgb;
        fArray[0] = f;
        fArray[1] = f2;
        fArray[2] = f3;
        fArray[3] = f4;
    }

    public void setUpperLeftRgb(float[] fArray, float f) {
        this.setUpperLeftRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void setUpperRightRgb(float f, float f2, float f3, float f4) {
        float[] fArray = this.upperRightRgb;
        fArray[0] = f;
        fArray[1] = f2;
        fArray[2] = f3;
        fArray[3] = f4;
    }

    public void setUpperRightRgb(float[] fArray, float f) {
        this.setUpperRightRgb(fArray[0], fArray[1], fArray[2], f);
    }

    public void writeColors(float[] fArray, int n) {
        float[] fArray2 = this.upperLeftRgb;
        fArray[n] = fArray2[0];
        fArray[n + 1] = fArray2[1];
        fArray[n + 2] = fArray2[2];
        fArray[n + 3] = fArray2[3];
        float[] fArray3 = this.upperRightRgb;
        fArray[n + 4] = fArray3[0];
        fArray[n + 5] = fArray3[1];
        fArray[n + 6] = fArray3[2];
        fArray[n + 7] = fArray3[3];
        fArray3 = this.lowerRightRgb;
        fArray[n + 8] = fArray3[0];
        fArray[n + 9] = fArray3[1];
        fArray[n + 10] = fArray3[2];
        fArray[n + 11] = fArray3[3];
        fArray[n + 12] = fArray2[0];
        fArray[n + 13] = fArray2[1];
        fArray[n + 14] = fArray2[2];
        fArray[n + 15] = fArray2[3];
        fArray[n + 16] = fArray3[0];
        fArray[n + 17] = fArray3[1];
        fArray[n + 18] = fArray3[2];
        fArray[n + 19] = fArray3[3];
        fArray2 = this.lowerLeftRgb;
        fArray[n + 20] = fArray2[0];
        fArray[n + 21] = fArray2[1];
        fArray[n + 22] = fArray2[2];
        fArray[n + 23] = fArray2[3];
    }

    public enum Origin {
        CENTER,
        BOTTOM_CENTER;

    }
}

