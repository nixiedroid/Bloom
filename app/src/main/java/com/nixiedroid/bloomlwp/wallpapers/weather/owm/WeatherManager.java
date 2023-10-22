package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.weather.AbstractWeatherManager;
import com.nixiedroid.bloomlwp.wallpapers.weather.TimeUtil;
import com.nixiedroid.bloomlwp.wallpapers.weather.WeatherVo;

public class WeatherManager extends AbstractWeatherManager {

    public WeatherManager(Context context) {
        super(context);

    }

    @Override
    protected void doGet() {
        boolean hasLocationPermission = ContextCompat.checkSelfPermission(App.get(), "android.permission.ACCESS_FINE_LOCATION") == 0;

        if (!hasLocationPermission) {
            L.w("no permissions");
            this.afterResult(AbstractWeatherManager.Result.FAILED_NO_PERMISSION);
            return;
        }
        try {
            previousResult = result;
            result = new WeatherVo();
            result.conditions = new int[]{4};
            resultTime = TimeUtil.nowMs();
            L.v("successful result: " + result.toString());
            afterResult(Result.OKAY, result);
        } catch (SecurityException var3) {
            L.w("no permissions");
            this.afterResult(AbstractWeatherManager.Result.FAILED_NO_PERMISSION);
        }
    }
    private WeatherVo weatherVoFromWeather(CurrentWeather curWeather) {
        WeatherVo weatherVo = new WeatherVo();
        int[] conditions = new int[curWeather.getWeather().size()];
        //"UNKNOWN", "CLEAR", "CLOUDY", "FOGGY", "HAZY", "ICY", "RAINY", "SNOWY", "STORMY", "WINDY"
        for (int i =0; i<conditions.length; i++){
            if (curWeather.getWeather().get(i).getId() < 300) {
                conditions[i] = 8;
                continue;
            }
            if (curWeather.getWeather().get(i).getId() < 400) {
                conditions[i] = 4;
                continue;
            }
            if (curWeather.getWeather().get(i).getId() < 600) {
                conditions[i] = 8;
                continue;
            }
            if (curWeather.getWeather().get(i).getId() < 700) {
                conditions[i] = 7;
                continue;
            }
            if (curWeather.getWeather().get(i).getId() < 800) {
                conditions[i] = 3;
                continue;
            }
            if (curWeather.getWeather().get(i).getId() == 800) {
                conditions[i] = 1;
                continue;
            }
            conditions[i] = 2;
        }
        weatherVo.conditions = conditions;
        if (conditions.length == 0) {
            L.w("bad value was returned; using 'UNKNOWN");
            weatherVo.conditions = new int[]{0};
        }

        return weatherVo;
    }
}
