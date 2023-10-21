/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.shadow;

import android.app.WallpaperColors;
import android.graphics.Color;

public class Configs {
    public static Config config1;
    public static Config config2;
    public static Config config3;
    public static Config config4;
    public static Config config5;
    public static Config config6;

    public static void init() {
        Config config = config1 = new Config(new String[]{"#b4b4b4", "#c8c8c8", "#d8d8d8", "#e2e2e2"}, "#f3f3f3");
        config.shadowAlpha = 1.0f;
        config.highlightAlpha = 0.1f;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-2565928), null, null, 1);
        config = config2 = new Config(new String[]{"#25211b", "#312e29", "#413d37", "#4c4640"}, "#565049");
        config.shadowAlpha = 0.75f;
        config.highlightAlpha = 0.02f;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-11778496), null, null, 2);
        config = config3 = new Config(new String[]{"#354186", "#40509f", "#475dad", "#556fce"}, "#5f80dd");
        config.shadowAlpha = 1.0f;
        config.highlightAlpha = 0.05f;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-13287034), null, null, 0);
        config = config4 = new Config(new String[]{"#5e7774", "#6c8c89", "#86a8a3", "#9ec6c1"}, "#b4e6e1");
        config.shadowAlpha = 1.0f;
        config.highlightAlpha = 0.1f;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-6371647), null, null, 0);
        config = config5 = new Config(new String[]{"#f8acaa", "#f4bdbd", "#f4cbcb", "#f2d7d7"}, "#efe4e4");
        config.shadowAlpha = 1.0f;
        config.highlightAlpha = 0.1f;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-862249), null, null, 1);
        config = config6 = new Config(new String[]{"#141822", "#181e2c", "#21293a", "#2f3b51"}, "#364660");
        config.shadowAlpha = 1.0f;
        config.highlightAlpha = 0.02f;
        config.wallpaperColors = new WallpaperColors(Color.valueOf(-13681839), null, null, 2);
    }
}

