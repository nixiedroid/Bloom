package com.nixiedroid.bloomlwp.weather.owm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.weather.AbstractWeatherManager;
import com.nixiedroid.bloomlwp.weather.LocationManger;
import com.nixiedroid.bloomlwp.weather.TimeUtil;

public class WeatherManager extends AbstractWeatherManager {

    private static final int WEATHER_THROTTLE_MS = 30 * 60 * 1000;
    private String API_KEY;
    private final BroadcastReceiver apiKeyUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context object, Intent intent) {
            final String key = intent.getStringExtra("preferences_api_key_update");
            L.v("got preferences_api_key_update - " + key);
            API_KEY = key;
        }
    };
    private OWMWeatherCode weather = null;
    public static void updateWeatherNow(){
       App.preferences().edit().putLong("prevUpdateTime", 0).apply();
    }

    public WeatherManager(Context context) {
        super();
        IntentFilter intentFilter = new IntentFilter("preferences_api_key_update");
        LocalBroadcastManager.getInstance(App.get()).registerReceiver(this.apiKeyUpdate, intentFilter);
        API_KEY = App.preferences().getString("API_KEY", App.get().getResources().getString(R.string.OWM_API_KEY));
        if (API_KEY.equals("0")) {
            L.w("API KEy not set");
            //return;
        }
        L.v("Using API Key: " + API_KEY);
    }

    private void getWeather(double lat, double lon) {
        try {
            OWMWeatherCode code = HttpUrlConnector.getWeatherCode(API_KEY, lat, lon);
            L.v("Got weather " + code.getDescription());

            weather = code;
        } catch (OWMConnectorException e) {
            L.w("Error retrieving weather: " + e.getMessage());
            weather = null;
        }
    }

    @Override
    protected void doGet() {
        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != 0) {
            L.w("no permissions");
            this.afterResult(Result.FAILED_NO_PERMISSION);
            return;
        }
        long tempTime = TimeUtil.elapsedRealTimeSince(App.preferences().getLong("prevUpdateTime", 0L));
        if (tempTime < WEATHER_THROTTLE_MS) {
            L.d("Weather Throttling");
            this.afterResult(Result.STOPPED);
            return;
        }
        try {
            double latitude = LocationManger.get().getCoord().getLat();
            double longitude = LocationManger.get().getCoord().getLon();
            getWeather(latitude, longitude);
            if (weather != null) {
                result = weather.getWeatherVo();
                this.previousResult = result;
                this.resultTime = TimeUtil.nowMs();
                App.preferences().edit().putLong("prevUpdateTime", resultTime).apply();
                App.preferences().edit().putString("current_location", latitude + " " + longitude).apply();
                App.preferences().edit().putString("current_weather_condition", weather.getDescription()).apply();
                App.preferences().edit().putString("current_weather_update_time", Util.epochToString(resultTime)).apply();
                L.v("successful result: " + result.toString());
                afterResult(Result.OKAY, result);
            } else {
                afterResult(Result.FAILED);
            }
        } catch (SecurityException var3) {
            L.w("no permissions");
            this.afterResult(Result.FAILED_NO_PERMISSION);
        }
    }


    @Override
    public void destroy() {
        super.destroy();
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this.apiKeyUpdate);
    }
}