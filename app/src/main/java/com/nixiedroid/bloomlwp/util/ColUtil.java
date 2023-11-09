package com.nixiedroid.bloomlwp.util;

import android.graphics.Color;

public class ColUtil {

    public static float[] intToRgb(int color) {
        return new float[]{
                (float)Color.red(color) / 255.0f,
                (float)Color.green(color) / 255.0f,
                (float)Color.blue(color) / 255.0f};
    }

    public static void lerpRgb(float value, float[] first, float[] second, float[] output) {
        output[0] = MathUtil.lerp(value, first[0], second[0]);
        output[1] = MathUtil.lerp(value, first[1], second[1]);
        output[2] = MathUtil.lerp(value, first[2], second[2]);
    }

    public static int rgbToInt(float[] colorArray) {
        return Color.rgb((int)(colorArray[0] * 255.0f), (int)(colorArray[1] * 255.0f), (int)(colorArray[2] * 255.0f));
    }
    public static boolean isWhite( float[] color){
        double darkness = 1-(0.299*color[0] + 0.587*color[1] + 0.114*color[2]);
        return darkness < 0.5;
    }


    public static float[] stringToRgb(String string2) {
        return ColUtil.intToRgb(Color.parseColor(string2));
    }
}

