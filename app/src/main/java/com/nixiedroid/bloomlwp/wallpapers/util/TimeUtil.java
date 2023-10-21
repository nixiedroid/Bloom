/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.wallpapers.Constants;
import java.util.Calendar;

public class TimeUtil {
    private static SharedPreferences cachePrefs;
    private static float sunriseDayPercent;
    private static float sunsetDayPercent;
    private static long timezoneOffsetMs;
    private static long unlockTime;

    public static float dayPercentFromHoursMinutes(int n, float f) {
        return ((float)n + f / 60.0f) / 24.0f;
    }

    public static float daytimePercent() {
        return MathUtil.normalize(TimeUtil.nowDayPercent(), sunriseDayPercent, sunsetDayPercent);
    }

    public static float daytimePercentOneHour() {
        return 1.0f / ((sunsetDayPercent - sunriseDayPercent) * 24.0f);
    }

    public static long elapsedRealTimeSince(long l) {
        long l2;
        l = l2 = TimeUtil.nowMs() - l;
        if (Constants.DEBUG_ACCEL_TIME) {
            l = l2 / 1500L;
        }
        return l;
    }

    public static float epochMsToDayPercent(long l) {
        return (float)((l + timezoneOffsetMs) % 86400000L) / 8.64E7f;
    }

    public static void init(Context context) {
        TimeUtil.updateTimezone();
        cachePrefs = context.createDeviceProtectedStorageContext().getSharedPreferences("cache", 0);
        TimeUtil.setUnlockTime();
        sunriseDayPercent = cachePrefs.getFloat("Sunrise", 0.25f);
        sunsetDayPercent = cachePrefs.getFloat("Sunset", 0.75f);
    }

    public static boolean isDaytime() {
        boolean bl = TimeUtil.nowDayPercent() >= sunriseDayPercent && TimeUtil.nowDayPercent() <= sunsetDayPercent;
        return bl;
    }

    public static float nighttimePercent() {
        float f = TimeUtil.nowDayPercent() < sunsetDayPercent ? TimeUtil.nowDayPercent() + 1.0f : TimeUtil.nowDayPercent();
        float f2 = sunriseDayPercent;
        return MathUtil.normalize(f, sunsetDayPercent, f2 + 1.0f);
    }

    public static float nighttimePercentOneHour() {
        return 1.0f / ((1.0f - (sunsetDayPercent - sunriseDayPercent)) * 24.0f);
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

    public static void setAccelerated(boolean bl) {
        if (bl == Constants.DEBUG_ACCEL_TIME) {
            return;
        }
        Constants.DEBUG_ACCEL_TIME = bl;
        TimeUtil.setUnlockTime();
    }

    public static void setSunriseSunsetPercents(float f, float f2) {
        sunriseDayPercent = f;
        sunsetDayPercent = f2;
        cachePrefs.edit().putFloat("Sunrise", f).putFloat("Sunset", f2).apply();
    }

    public static void setUnlockTime() {
        unlockTime = TimeUtil.nowMs();
    }

    public static float sunriseDayPercent() {
        return sunriseDayPercent;
    }

    public static float sunsetDayPercent() {
        return sunsetDayPercent;
    }

    public static long unlockTime() {
        return unlockTime;
    }

    public static void updateTimezone() {
        Calendar calendar = Calendar.getInstance();
        timezoneOffsetMs = calendar.get(15) + calendar.get(16);
    }
}

