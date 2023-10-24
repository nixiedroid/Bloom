package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

import android.graphics.PointF;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import android.Manifest;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.weather.AbstractSunriseUtil;
import com.nixiedroid.bloomlwp.wallpapers.weather.TimeUtil;
public class SunriseUtil extends AbstractSunriseUtil {

    public SunriseUtil() {

    }

    @Override
    protected void doGet() {
        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != 0) {
            L.w("no permissions");
            this.afterResult(AbstractSunriseUtil.Result.FAILED_NO_PERMISSION);
            return;
        }
        float latitude = (float) LocationManger.get().getCoord().getLat();
        float longitude = (float) LocationManger.get().getCoord().getLon();
        L.v("location: " + latitude + ", " + longitude);
        float[] sunriseSunsetTime = AbstractSunriseUtil.getSunriseSunset(latitude, longitude);
        L.v("sunrise/sunset: " + sunriseSunsetTime[0] + ", " + sunriseSunsetTime[1]);
       // latLon = new PointF(latitude, longitude);
        sunriseDayPercent = sunriseSunsetTime[0];
        sunsetDayPercent = sunriseSunsetTime[1];
        TimeUtil.setSunriseSunsetPercents(sunriseSunsetTime[0], sunriseSunsetTime[1]);
        afterResult(Result.OKAY);

    }
}
