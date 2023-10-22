package com.nixiedroid.bloomlwp.util;

import android.view.Surface;

public class Util {
    public static void adjustGravityVectorForDisplayRotation(int rotation, float[] input, float[] output) {
        float rotY =0;
        float rotX =0;
        switch (rotation) {
            case Surface.ROTATION_0:
                rotX = input[0];
                rotY = input[1];
                break;
            case Surface.ROTATION_90:
                rotX = input[1] * -1.0f;
                rotY = input[0];
                break;
            case Surface.ROTATION_180:
                rotX = input[0] * -1.0f;
                rotY = -1.0f * input[1];
                break;
            case Surface.ROTATION_270:
                rotX = input[1];
                rotY = -1.0f * input[0];
                break;
        }
        output[0] = rotX;
        output[1] = rotY;
        output[2] = input[2];
    }
}

