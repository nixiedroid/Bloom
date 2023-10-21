package com.nixiedroid.bloomlwp.wallpapers.util.owm;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.util.AbstractWeatherManager;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;
import com.nixiedroid.bloomlwp.wallpapers.util.WeatherVo;
import com.nixiedroid.owm.implementation.OpenWeatherMapHelper;
import com.nixiedroid.owm.implementation.callback.CurrentWeatherCallback;
import com.nixiedroid.owm.model.common.Clouds;
import com.nixiedroid.owm.model.common.Weather;
import com.nixiedroid.owm.model.currentweather.CurrentWeather;

public class WeatherManager extends AbstractWeatherManager {
    OpenWeatherMapHelper helper;
    public WeatherManager(Context context) {
        super(context);
        helper = new OpenWeatherMapHelper(App.get().getString(R.string.OPEN_WEATHER_MAP_API_KEY));
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
            L.v("getting weather from OWN");
            helper.getCurrentWeatherByCityName("Minsk", new CurrentWeatherCallback() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    previousResult = result;
                    result = weatherVoFromWeather(currentWeather);
                    resultTime = TimeUtil.nowMs();
                    L.v("successful result: " + result.toString());
                    afterResult(Result.OKAY, result);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });


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
