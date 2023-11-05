package com.nixiedroid.bloomlwp.weather;

import com.nixiedroid.bloomlwp.util.L;

public class SunriseUtil extends AbstractSunriseUtil {

    public SunriseUtil() {

    }

    @Override
    protected void doGet() {
        float latitude = (float) LocationManger.get().getCoord().getLat();
        float longitude = (float) LocationManger.get().getCoord().getLon();
        float[] sunriseSunsetTime = getSunriseSunset(latitude, longitude);
        L.v("sunrise/sunset: " + sunriseSunsetTime[0] + ", " + sunriseSunsetTime[1]);
        TimeUtil.setSunriseSunsetPercents(sunriseSunsetTime[0], sunriseSunsetTime[1]);
        afterResult();
    }
}
