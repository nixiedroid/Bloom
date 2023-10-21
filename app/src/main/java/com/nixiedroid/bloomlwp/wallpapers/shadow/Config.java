/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.shadow;

import android.app.WallpaperColors;
import android.graphics.Color;
import com.nixiedroid.bloomlwp.util.ColUtil;

public class Config {
    public static final float[] shadowAlphaMultiplierByWeatherCondition = new float[]{0.4f, 0.4f, 0.3f, 0.3f, 0.25f, 0.4f, 0.3f, 0.3f, 0.3f, 0.4f};
    public static final float[] shadowGradientLength1440ByWeatherCondition = new float[]{5.0f, 5.0f, 10.0f, 30.0f, 20.0f, 5.0f, 10.0f, 10.0f, 10.0f, 5.0f};
    public float highlightAlpha;
    public final int[] mainColors;
    public float shadowAlpha;
    public final int surfaceColor;
    public final float[] surfaceColorRgb;
    public WallpaperColors wallpaperColors;

    public Config(String[] stringArray, String string2) {
        this.mainColors = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; ++i) {
            int n = stringArray.length;
            this.mainColors[i] = Color.parseColor(stringArray[n - i - 1]);
        }
        this.surfaceColor = Color.parseColor(string2);
        this.surfaceColorRgb = ColUtil.intToRgb(this.surfaceColor);
    }
}

