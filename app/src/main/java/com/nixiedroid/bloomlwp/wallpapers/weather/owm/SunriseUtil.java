package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

import android.graphics.PointF;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.weather.AbstractSunriseUtil;
import com.nixiedroid.bloomlwp.wallpapers.weather.TimeUtil;
public class SunriseUtil extends AbstractSunriseUtil {

    public SunriseUtil() {

    }

    @Override
    protected void doGet() {
        if (ContextCompat.checkSelfPermission(App.get(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
            L.w("no permissions");
            this.afterResult(AbstractSunriseUtil.Result.FAILED_NO_PERMISSION);
            return;
        }
        Coord location = new Coord(53.926,27.433);
        float latitude = (float) location.getLat();
        float longitude = (float) location.getLon();
        L.v("location: " + latitude + ", " + longitude);
        float[] sunriseSunsetTime = AbstractSunriseUtil.getSunriseSunset(latitude, longitude);
        if (sunriseSunsetTime == null) {
            L.w("sunrise values from lat lon failed");
            afterResult(Result.FAILED);
            return;
        }
        L.v("sunrise/sunset: " + sunriseSunsetTime[0] + ", " + sunriseSunsetTime[1]);
        latLon = new PointF(latitude, longitude);
        sunriseDayPercent = sunriseSunsetTime[0];
        sunsetDayPercent = sunriseSunsetTime[1];
        TimeUtil.setSunriseSunsetPercents(sunriseSunsetTime[0], sunriseSunsetTime[1]);
        afterResult(Result.OKAY);

    }
}
