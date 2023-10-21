package com.nixiedroid.bloomlwp.wallpapers.shadow;

import android.app.WallpaperColors;
import android.graphics.Color;
import android.os.Build;

public class Configs {
    public static Config config1;
    public static Config config2;
    public static Config config3;
    public static Config config4;
    public static Config config5;
    public static Config config6;

    public static void init() {
        config1 = new Config(new String[]{"#b4b4b4", "#c8c8c8", "#d8d8d8", "#e2e2e2"}, "#f3f3f3");
        config1.shadowAlpha = 1.0f;
        config1.highlightAlpha = 0.1f;
        config1.wallpaperColors =  setColors(-2565928, 1);
        config2 = new Config(new String[]{"#25211b", "#312e29", "#413d37", "#4c4640"}, "#565049");
        config2.shadowAlpha = 0.75f;
        config2.highlightAlpha = 0.02f;
        config2.wallpaperColors =  setColors(-11778496,2);
        config3 = new Config(new String[]{"#354186", "#40509f", "#475dad", "#556fce"}, "#5f80dd");
        config3.shadowAlpha = 1.0f;
        config3.highlightAlpha = 0.05f;
        config3.wallpaperColors = setColors(-13287034,0);
        config4 = new Config(new String[]{"#5e7774", "#6c8c89", "#86a8a3", "#9ec6c1"}, "#b4e6e1");
        config4.shadowAlpha = 1.0f;
        config4.highlightAlpha = 0.1f;
        config4.wallpaperColors = setColors(-6371647,0);
        config5 = new Config(new String[]{"#f8acaa", "#f4bdbd", "#f4cbcb", "#f2d7d7"}, "#efe4e4");
        config5.shadowAlpha = 1.0f;
        config5.highlightAlpha = 0.1f;
        config5.wallpaperColors =  setColors(-862249,1);
        config6 = new Config(new String[]{"#141822", "#181e2c", "#21293a", "#2f3b51"}, "#364660");
        config6.shadowAlpha = 1.0f;
        config6.highlightAlpha = 0.02f;
        config6.wallpaperColors =  setColors(-13681839,2);
    }
    private static  WallpaperColors setColors(int color, int colorHints){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new WallpaperColors(Color.valueOf(color), null, null, colorHints);
        }
        return new WallpaperColors(Color.valueOf(color), null, null);
    }
}

