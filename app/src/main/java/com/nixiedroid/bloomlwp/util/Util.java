package com.nixiedroid.bloomlwp.util;

import android.view.Surface;

public class Util {
    public static void adjustGravityVectorForDisplayRotation(int rotation, float[] input, float[] output) {
        float f2 =0;
        float f3 =0;
        switch (rotation) {
            case Surface.ROTATION_0:
                f3 = input[0];
                f2 = input[1];
                break;
            case Surface.ROTATION_90:
                f3 = input[1] * -1.0f;
                f2 = input[0];
                break;
            case Surface.ROTATION_180:
                f3 = input[0] * -1.0f;
                f2 = -1.0f * input[1];
                break;
            case Surface.ROTATION_270:
                f3 = input[1];
                f2 = -1.0f * input[0];
                break;
        }
        output[0] = f3;
        output[1] = f2;
        output[2] = input[2];
    }
}

