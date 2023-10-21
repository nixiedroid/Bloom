package com.nixiedroid.bloomlwp.wallpapers.orbit;

import android.app.WallpaperColors;
import android.graphics.Color;
import android.os.Build;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.ColUtil;

public class Configs {
    public static Config config1;
    public static Config config2;
    public static Config config3;

    public static void init() {
        initLight();
        initBlue();
        initDark();
    }

    private static void initLight() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            config.wallpaperColors = new WallpaperColors(
                    Color.valueOf(0xFF_EF_EF_EF), null, null, WallpaperColors.HINT_SUPPORTS_DARK_TEXT);
        } else {
            config.wallpaperColors = new WallpaperColors(
                    Color.valueOf(0xFF_EF_EF_EF), null, null);
        }
        initCommon(config);
        config1 = config;
    }

    private static void initDark() {
        Config config = new Config();
        config.dotRgb = ColUtil.stringToRgb("#ff2234");
        config.squareRgb = ColUtil.stringToRgb("#c93d3d");
        config.squareCircleOverlapRgb = ColUtil.stringToRgb("#611616");
        config.dotCircleOverlapRgb = ColUtil.stringToRgb("#ff2234");
        config.dotSquareOverlapRgb = ColUtil.stringToRgb("#ff2234");
        config.allOverlapRgb = ColUtil.stringToRgb("#ff2234");
        config.backgroundRgb = ColUtil.stringToRgb("#343434");
        config.circleTexture1440ResourceId = R.drawable.orbit_gradient_dark_616_1440;
        config.circleTexture1080ResourceId = R.drawable.orbit_gradient_dark_616_1080;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            config.wallpaperColors = new WallpaperColors(
                    Color.valueOf(0xFF_10_10_10), null, null, WallpaperColors.HINT_SUPPORTS_DARK_THEME);
        } else {
            config.wallpaperColors = new WallpaperColors(
                    Color.valueOf(0xFF_10_10_10), null, null);
        }
        initCommon(config);
        config2 = config;
    }

    private static void initBlue() {
        Config config = new Config();
        config.dotRgb = ColUtil.stringToRgb("#81C8E5");
        config.squareRgb = ColUtil.stringToRgb("#EDBFC8");
        config.squareCircleOverlapRgb = ColUtil.stringToRgb("#6D5DFD");
        config.dotCircleOverlapRgb = ColUtil.stringToRgb("#81C8E5");
        config.dotSquareOverlapRgb = ColUtil.stringToRgb("#81C8E5");
        config.allOverlapRgb = ColUtil.stringToRgb("#81C8E5");
        config.backgroundRgb = ColUtil.stringToRgb("#4C53FA");
        config.circleTexture1440ResourceId = R.drawable.orbit_gradient_blue_615_1440;
        config.circleTexture1080ResourceId = R.drawable.orbit_gradient_blue_615_1080;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            config.wallpaperColors = new WallpaperColors(
                    Color.valueOf(0xFF_4C_53_FA), null, null, 0);
        } else {
            config.wallpaperColors = new WallpaperColors(
                    Color.valueOf(0xFF_4C_53_FA), null, null);
        }
        initCommon(config);
        config3 = config;
    }
    private static void initCommon(Config config){
        config.dotRadius1440 = 20.0f;
        config.squareRadius1440 = 175.0f;
        config.circleRadius1440 = 768.0f;
        config.doesDotOrbitCircle = true;
        config.dotOrbitRadius1440 = 629.76f;
        config.squareOrbitRadius1440 = 512.0f;
        config.circleOrbitRadius1440 = 512.0f;
    }
}

