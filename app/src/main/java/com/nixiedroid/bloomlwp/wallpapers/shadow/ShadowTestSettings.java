package com.nixiedroid.bloomlwp.wallpapers.shadow;

public class ShadowTestSettings {
    public boolean isPulseDisabled;
    public TimeType timeType;
    public boolean useAccelTime;
    public boolean useFastRandomWeather;
    public int weatherIndex;

    public static enum TimeType {
        DAYTIME,
        SUNRISE_SUNSET,
        NIGHTTIME;

    }
}

