package com.nixiedroid.bloomlwp.wallpapers.util.owm;

import android.graphics.PointF;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.util.AbstractSunriseUtil;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;
import com.nixiedroid.owm.implementation.OpenWeatherMapHelper;
import com.nixiedroid.owm.implementation.callback.CurrentWeatherCallback;
import com.nixiedroid.owm.model.common.Coord;
import com.nixiedroid.owm.model.currentweather.CurrentWeather;

public class SunriseUtil extends AbstractSunriseUtil {
    OpenWeatherMapHelper helper;
    public SunriseUtil() {
      helper = new OpenWeatherMapHelper(App.get().getString(R.string.OPEN_WEATHER_MAP_API_KEY));
    }

    @Override
    protected void doGet() {
        if (ContextCompat.checkSelfPermission(App.get(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
            L.w("no permissions");
            this.afterResult(AbstractSunriseUtil.Result.FAILED_NO_PERMISSION);
            return;
        }
        helper.getCurrentWeatherByCityName("Minsk", new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                Coord location = currentWeather.getCoord();
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

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }
}
