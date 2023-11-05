package com.nixiedroid.bloomlwp.weather;

import com.nixiedroid.bloomlwp.events.SunriseResult;
import com.nixiedroid.bloomlwp.util.L;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public abstract class AbstractSunriseUtil {
    protected float sunriseDayPercent;
    protected float sunsetDayPercent;

    @NotNull
    @Contract("_, _ -> new")
    protected static float[] getSunriseSunset(float latitude, float longitude) {
        CalendarAstronomer calendarAstronomer = new CalendarAstronomer(longitude, latitude);
        long sunRise = calendarAstronomer.getSunRiseSet(true);
        long sunSet = calendarAstronomer.getSunRiseSet(false);
        L.v("sunrise : " + new Date(sunRise));
        L.v("sunset  : " + new Date(sunSet));
        return new float[]{TimeUtil.epochMsToDayPercent(sunRise), TimeUtil.epochMsToDayPercent(sunSet)};
    }

    protected void afterResult() {
        EventBus.getDefault().post(new SunriseResult());
    }

    protected abstract void doGet();

    public void get() {
        L.v("Getting location");
        this.doGet();
    }

}

