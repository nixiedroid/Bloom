package com.nixiedroid.bloomlwp.wallpapers.weather;

import android.content.Intent;
import android.graphics.PointF;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;

import java.util.Date;

public abstract class AbstractSunriseUtil {
    protected PointF latLon;
    protected float sunriseDayPercent;
    protected float sunsetDayPercent;

    public static float[] getSunriseSunset(float latitude, float longitude) {
        CalendarAstronomer calendarAstronomer = new CalendarAstronomer(longitude, latitude);
        long sunRiseSet = calendarAstronomer.getSunRiseSet(true);
        long sunRiseSet2 = calendarAstronomer.getSunRiseSet(false);
        L.v("sunrise : " + new Date(sunRiseSet));
        L.v("sunset  : " + new Date(sunRiseSet2));
        return new float[]{TimeUtil.epochMsToDayPercent(sunRiseSet), TimeUtil.epochMsToDayPercent(sunRiseSet2)};
    }

    protected void afterResult(Result result) {
        Intent intent = new Intent("sunrise_result");
        intent.putExtra("sunrise_result", result == Result.OKAY);
        LocalBroadcastManager.getInstance(App.get()).sendBroadcast(intent);
    }

    protected abstract void doGet();

    public void get() {
        this.doGet();
    }

    public enum Result {
        OKAY,
        FAILED,
        FAILED_NO_PERMISSION,
        STOPPED

    }
}

