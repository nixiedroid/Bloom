package com.nixiedroid.bloomlwp.util;

public class Util {
    public static void adjustGravityVectorForDisplayRotation(int n, float[] fArray, float[] fArray2) {
        float f2;
        float f3;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    f3 = fArray[0];
                    f2 = fArray[1];
                } else {
                    f3 = fArray[1];
                    f2 = -1.0f * fArray[0];
                }
            } else {
                f3 = fArray[0] * -1.0f;
                f2 = -1.0f * fArray[1];
            }
        } else {
            f3 = fArray[1] * -1.0f;
            f2 = fArray[0];
        }
        fArray2[0] = f3;
        fArray2[1] = f2;
        fArray2[2] = fArray[2];
    }
}

