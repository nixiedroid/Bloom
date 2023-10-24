package com.nixiedroid.bloomlwp.wallpapers.weather;

import android.content.Context;
import android.content.SharedPreferences;
import com.nixiedroid.bloomlwp.wallpapers.Constants;

import java.util.Calendar;

public class TimeUtil {
    private static SharedPreferences cachePrefs;
    private static float sunriseDayPercent;
    private static float sunsetDayPercent;
    private static long timezoneOffsetMs;

    public static long elapsedRealTimeSince(final long epochTime) {
        long deltaTime = TimeUtil.nowMs() - epochTime;
        if (Constants.DEBUG_ACCEL_TIME) {
            deltaTime = deltaTime / 1500L;
        }
        return deltaTime;
    }

    public static float epochMsToDayPercent(long epochTime) {
        return (float)((epochTime + timezoneOffsetMs) % 86400000L) / 8.64E7f;
    }

    public static void init(Context context) {
        TimeUtil.updateTimezone();
        cachePrefs = context.createDeviceProtectedStorageContext().getSharedPreferences("cache", 0);
        sunriseDayPercent = cachePrefs.getFloat("Sunrise", 0.25f);
        sunsetDayPercent = cachePrefs.getFloat("Sunset", 0.75f);
    }

    public static float nowDayPercent() {
        return TimeUtil.epochMsToDayPercent(TimeUtil.nowMs());
    }

    public static long nowMs() {
        if (Constants.DEBUG_ACCEL_TIME) {
            return System.currentTimeMillis() * 1500L;
        }
        return System.currentTimeMillis();
    }

    public static void setAccelerated(boolean isAccelerated) {
        Constants.DEBUG_ACCEL_TIME = isAccelerated;
    }

    public static void setSunriseSunsetPercents(float sunrise, float sunset) {
        sunriseDayPercent = sunrise;
        sunsetDayPercent = sunset;
        cachePrefs.edit().putFloat("Sunrise", sunrise).putFloat("Sunset", sunset).apply();
    }
    public static float sunriseDayPercent() {
        return sunriseDayPercent;
    }

    public static float sunsetDayPercent() {
        return sunsetDayPercent;
    }

    public static void updateTimezone() {
        Calendar calendar = Calendar.getInstance();
        timezoneOffsetMs = calendar.get(15) + calendar.get(16);
    }
}

