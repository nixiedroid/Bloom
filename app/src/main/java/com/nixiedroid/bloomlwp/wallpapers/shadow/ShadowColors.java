/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.shadow;

import com.nixiedroid.bloomlwp.util.ColUtil;
import com.nixiedroid.bloomlwp.util.MathUtil;

public class ShadowColors {
    public static float[] dayShadowRgb = ColUtil.stringToRgb("#b0bec5");
    private static float[] highlightRgb;
    private static float[] nightShadowRgb;
    private static float[] sunriseSunsetShadowRgb;

    static {
        sunriseSunsetShadowRgb = ColUtil.stringToRgb("#c3bdb1");
        nightShadowRgb = ColUtil.stringToRgb("#666c8c");
        highlightRgb = ColUtil.stringToRgb("#ffffff");
    }

    public static float[] daytimeShadowRgb(float f, float f2) {
        float f3 = f2 * 1.0f;
        if (f < f3) {
            return sunriseSunsetShadowRgb;
        }
        if (f < (f2 *= 2.0f)) {
            return ColUtil.lerpRgb(MathUtil.normalize(f, f3, f2), sunriseSunsetShadowRgb, dayShadowRgb);
        }
        if (f < (f2 = 1.0f - f2)) {
            return dayShadowRgb;
        }
        if (f < (f3 = 1.0f - f3)) {
            return ColUtil.lerpRgb(MathUtil.normalize(f, f2, f3), dayShadowRgb, sunriseSunsetShadowRgb);
        }
        return sunriseSunsetShadowRgb;
    }

    public static float[] highlightRgb() {
        return highlightRgb;
    }

    public static void init() {
    }

    public static float[] nightimeShadowRgb(float f, float f2) {
        float f3 = f2 * 1.0f;
        if (f < f3) {
            return sunriseSunsetShadowRgb;
        }
        if (f < (f2 *= 2.0f)) {
            return ColUtil.lerpRgb(MathUtil.normalize(f, f3, f2), sunriseSunsetShadowRgb, nightShadowRgb);
        }
        if (f < (f2 = 1.0f - f2)) {
            return nightShadowRgb;
        }
        if (f < (f3 = 1.0f - f3)) {
            return ColUtil.lerpRgb(MathUtil.normalize(f, f2, f3), nightShadowRgb, sunriseSunsetShadowRgb);
        }
        return sunriseSunsetShadowRgb;
    }
}

