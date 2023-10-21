package com.nixiedroid.bloomlwp.util;

import android.graphics.Color;

public class ColUtil {

    public static float[] intToRgb(int n) {
        int n2 = Color.red(n);
        int n3 = Color.green(n);
        n = Color.blue(n);
        return new float[]{(float)n2 / 255.0f, (float)n3 / 255.0f, (float)n / 255.0f};
    }

    public static void lerpRgb(float f, float[] fArray, float[] fArray2, float[] fArray3) {
        fArray3[0] = MathUtil.lerp(f, fArray[0], fArray2[0]);
        fArray3[1] = MathUtil.lerp(f, fArray[1], fArray2[1]);
        fArray3[2] = MathUtil.lerp(f, fArray[2], fArray2[2]);
    }

    public static int rgbToInt(float[] fArray) {
        return Color.rgb((int)(fArray[0] * 255.0f), (int)(fArray[1] * 255.0f), (int)(fArray[2] * 255.0f));
    }

    public static float[] stringToRgb(String string2) {
        return ColUtil.intToRgb(Color.parseColor(string2));
    }
}

