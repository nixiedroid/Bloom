package com.nixiedroid.bloomlwp.wallpapers.orbit;

import android.app.WallpaperColors;

public class Config {
    public float[] allOverlapRgb;
    public float[] backgroundRgb;
    public float circleOrbitRadius1440 = 720.0f;
    public float circleRadius1440 = 960.0f;
    public int circleTexture1080ResourceId;
    public int circleTexture1440ResourceId;
    public boolean doesCircleGradientRotate = true;
    public boolean doesDotOrbitCircle = false;
    public float[] dotCircleOverlapRgb;
    public float dotOrbitRadius1440 = 720.0f;
    public float dotRadius1440 = 60.0f;
    public float[] dotRgb = new float[3];
    public float[] dotSquareOverlapRgb;
    public float[] squareCircleOverlapRgb;
    public float squareOrbitRadius1440 = 720.0f;
    public float squareRadius1440 = 240.0f;
    public float[] squareRgb = new float[3];
    public WallpaperColors wallpaperColors;

    public Config() {
        this.backgroundRgb = new float[3];
        this.dotCircleOverlapRgb = new float[3];
        this.squareCircleOverlapRgb = new float[3];
        this.dotSquareOverlapRgb = new float[3];
        this.allOverlapRgb = new float[3];
    }
}

