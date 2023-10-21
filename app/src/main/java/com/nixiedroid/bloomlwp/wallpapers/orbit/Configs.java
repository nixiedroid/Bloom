/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.orbit;

import android.app.WallpaperColors;
import android.graphics.Color;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.ColUtil;

public class Configs {
    public static Config config1;
    public static Config config2;
    public static Config config3;

    public static void init() {
        Config config = new Config();
        config.dotRgb = ColUtil.stringToRgb("#F9D716");
        config.squareRgb = ColUtil.stringToRgb("#4D3AFF");
        config.squareCircleOverlapRgb = ColUtil.stringToRgb("#8B7FFF");
        config.dotCircleOverlapRgb = ColUtil.stringToRgb("#F9D716");
        config.dotSquareOverlapRgb = ColUtil.stringToRgb("#F9D716");
        config.allOverlapRgb = ColUtil.stringToRgb("#F9D716");
        config.backgroundRgb = ColUtil.stringToRgb("#efefef");
        config.circleTexture1440ResourceId = R.drawable.orbit_gradient_62_light_1440;
        config.circleTexture1080ResourceId = R.drawable.orbit_gradient_62_light_1080;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-1052689), null, null, 1);
        config.dotRadius1440 = 20.0f;
        config.squareRadius1440 = 175.0f;
        config.circleRadius1440 = 768.0f;
        config.doesDotOrbitCircle = true;
        config.dotOrbitRadius1440 = 629.76f;
        config.squareOrbitRadius1440 = 512.0f;
        config.circleOrbitRadius1440 = 512.0f;
        config1 = config;

        config = new Config();
        config.dotRgb = ColUtil.stringToRgb("#ff2234");
        config.squareRgb = ColUtil.stringToRgb("#c93d3d");
        config.squareCircleOverlapRgb = ColUtil.stringToRgb("#611616");
        config.dotCircleOverlapRgb = ColUtil.stringToRgb("#ff2234");
        config.dotSquareOverlapRgb = ColUtil.stringToRgb("#ff2234");
        config.allOverlapRgb = ColUtil.stringToRgb("#ff2234");
        config.backgroundRgb = ColUtil.stringToRgb("#343434");
        config.circleTexture1440ResourceId = R.drawable.orbit_gradient_blue_615_1440 ;
        config.circleTexture1080ResourceId = R.drawable.orbit_gradient_blue_615_1080;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-15724528), null, null, 2);
        config.dotRadius1440 = 20.0f;
        config.squareRadius1440 = 175.0f;
        config.circleRadius1440 = 768.0f;
        config.doesDotOrbitCircle = true;
        config.dotOrbitRadius1440 = 629.76f;
        config.squareOrbitRadius1440 = 512.0f;
        config.circleOrbitRadius1440 = 512.0f;
        config2 = config;

        config = new Config();
        config.dotRgb = ColUtil.stringToRgb("#81C8E5");
        config.squareRgb = ColUtil.stringToRgb("#EDBFC8");
        config.squareCircleOverlapRgb = ColUtil.stringToRgb("#6D5DFD");
        config.dotCircleOverlapRgb = ColUtil.stringToRgb("#81C8E5");
        config.dotSquareOverlapRgb = ColUtil.stringToRgb("#81C8E5");
        config.allOverlapRgb = ColUtil.stringToRgb("#81C8E5");
        config.backgroundRgb = ColUtil.stringToRgb("#4C53FA");
        config.circleTexture1440ResourceId = R.drawable.orbit_gradient_dark_616_1440;
        config.circleTexture1080ResourceId = R.drawable.orbit_gradient_dark_616_1080;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-11774982), null, null, 0);
        config.dotRadius1440 = 20.0f;
        config.squareRadius1440 = 175.0f;
        config.circleRadius1440 = 768.0f;
        config.doesDotOrbitCircle = true;
        config.dotOrbitRadius1440 = 629.76f;
        config.squareOrbitRadius1440 = 512.0f;
        config.circleOrbitRadius1440 = 512.0f;
        config3 = config;
    }
}

