package com.nixiedroid.bloomlwp.wallpapers.weather;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;

import java.util.Date;

public abstract class AbstractSunriseUtil {
    protected float sunriseDayPercent;
    protected float sunsetDayPercent;

    public static float[] getSunriseSunset(float latitude, float longitude) {
        CalendarAstronomer calendarAstronomer = new CalendarAstronomer(longitude, latitude);
        long sunRise = calendarAstronomer.getSunRiseSet(true);
        long sunSet = calendarAstronomer.getSunRiseSet(false);
        L.v("sunrise : " + new Date(sunRise));
        L.v("sunset  : " + new Date(sunSet));
        return new float[]{TimeUtil.epochMsToDayPercent(sunRise), TimeUtil.epochMsToDayPercent(sunSet)};
    }

    protected void afterResult() {
        Intent intent = new Intent("sunrise_result");
        intent.putExtra("sunrise_result", true);
        LocalBroadcastManager.getInstance(App.get()).sendBroadcast(intent);
    }

    protected abstract void doGet();

    public void get() {
        L.v("Getting location");
        this.doGet();
    }

}

