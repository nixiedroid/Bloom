package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callback.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.model.currentweather.CurrentWeather;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.bloom.BloomWallpaperService;
import com.nixiedroid.bloomlwp.wallpapers.weather.AbstractWeatherManager;
import com.nixiedroid.bloomlwp.wallpapers.weather.TimeUtil;
import com.nixiedroid.bloomlwp.wallpapers.weather.WeatherVo;

public class WeatherManager extends AbstractWeatherManager {

    private String API_KEY;
    private OpenWeatherMapHelper helper;
    private CurrentWeather weather = null;
    private static final int WEATHER_THROTTLE_MS = 30*60*1000;
    protected long previousResultTime;
    private final BroadcastReceiver apiKeyUpdate = new BroadcastReceiver(){
        @Override
        public void onReceive(Context object, Intent intent) {
            final String key = intent.getStringExtra("preferences_api_key_update");
            L.v("got preferences_api_key_update - " +key);
            API_KEY = key;
            helper = new OpenWeatherMapHelper(API_KEY);
        }
    };

    public WeatherManager(Context context) {
        super(context);
        IntentFilter intentFilter = new IntentFilter("preferences_api_key_update");
        LocalBroadcastManager.getInstance(App.get()).registerReceiver(this.apiKeyUpdate, intentFilter);
        API_KEY = cachePrefs.getString("API_KEY",App.get().getResources().getString(R.string.OWM_API_KEY));
        if (API_KEY.equals("0")){
            L.w("API KEy not set");
            //return;
        }
        L.v("Using API Key: " + API_KEY);
        helper = new OpenWeatherMapHelper(API_KEY);
        previousResultTime = cachePrefs.getLong("prevUpdateTime",0L);
    }

    private void getWeather(double lat, double lon){
        helper.getCurrentWeatherByGeoCoordinates(lat, lon, new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                L.v("Got weather " + currentWeather.getWeather().get(0).getId() + " : " + currentWeather.getWeather().get(0).getDescription());
               weather  = currentWeather;
            }
            @Override
            public void onFailure(Throwable throwable) {
                L.w("Error retrieving weather: " + throwable.getMessage());
                weather = null;
            }
        });
    }

    @Override
    protected void doGet() {
        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != 0) {
            L.w("no permissions");
            this.afterResult(Result.FAILED_NO_PERMISSION);
            return;
        }
        previousResultTime = cachePrefs.getLong("prevUpdateTime",0L);
        long tempTime = TimeUtil.elapsedRealTimeSince(previousResultTime);
        if (tempTime < WEATHER_THROTTLE_MS){
            L.d("Weather Throttling");
            this.afterResult(Result.STOPPED);
            return;
        }
        try {
            double latitude = LocationManger.get().getCoord().getLat();
            double longitude = LocationManger.get().getCoord().getLon();
            getWeather(latitude,longitude);
            if (weather != null) result = weatherVoFromWeather(weather);
            this.previousResult = result;
            this.resultTime = TimeUtil.nowMs();
            this.previousResultTime = resultTime;
            this.cachePrefs.edit().putLong("prevUpdateTime", previousResultTime).apply();

            L.v("successful result: " + result.toString());
            afterResult(Result.OKAY, result);
        } catch (SecurityException var3) {
            L.w("no permissions");
            this.afterResult(Result.FAILED_NO_PERMISSION);
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

    @Override
    public void destroy() {
        super.destroy();
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this.apiKeyUpdate);
    }
}
