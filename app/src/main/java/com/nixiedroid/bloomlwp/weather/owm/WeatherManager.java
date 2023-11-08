package com.nixiedroid.bloomlwp.weather.owm;

import android.widget.Toast;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.BuildConfig;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.events.ApiKeyUpdate;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.weather.AbstractWeatherManager;
import com.nixiedroid.bloomlwp.weather.LocationManger;
import com.nixiedroid.bloomlwp.weather.Result;
import com.nixiedroid.bloomlwp.weather.TimeUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WeatherManager extends AbstractWeatherManager {

    private static final int WEATHER_THROTTLE_MS = 30 * 60 * 1000;

    private String API_KEY;
    private OWMWeatherCode weather = null;
    /** @noinspection unused*/
    @Subscribe
    public void onEvent(ApiKeyUpdate event) {
        final String key = event.getApiKey();
        L.v("got preferences_api_key_update - " + key);
        API_KEY = key;
    }

    public WeatherManager() {
        super();
        EventBus.getDefault().register(this);
        if (BuildConfig.DEBUG) {
            API_KEY = App.preferences().getString("API_KEY", App.get().getResources().getString(R.string.OWM_API_KEY));
        } else {
            API_KEY = App.preferences().getString("API_KEY", "");
        }
        if (API_KEY.isEmpty()) {
            L.w("API key not set");
            Toast.makeText(
                    App.get(),
                    R.string.no_api_key_warning,
                    Toast.LENGTH_LONG
            ).show();
        }
        L.v("Using API Key: " + API_KEY);
    }



    private void getWeather(double lat, double lon) {
        try {
            OWMWeatherCode code = HttpUrlConnector.getWeatherCode(API_KEY, lat, lon);
            L.v("Got weather " + code.getDescription());

            weather = code;
        } catch (OWMConnectorException e) {
            L.e("Error retrieving weather: " + e.getMessage());
            Toast.makeText(App.get(),e.getMessage(),Toast.LENGTH_LONG).show();
            weather = null;
        }
    }

    @Override
    protected void doGet() {
        long tempTime = TimeUtil.elapsedRealTimeSince(App.preferences().getLong("prevUpdateTime", 0L));
        if (tempTime < WEATHER_THROTTLE_MS) {
            L.d("Weather Throttling");
            this.afterResult(Result.TOO_MANY_REQUESTS);
            return;
        }
        try {
            double latitude = LocationManger.get().getCoord().getLat();
            double longitude = LocationManger.get().getCoord().getLon();
            getWeather(latitude, longitude);
            if (weather != null) {
                result = weather.getWeatherVo();
                this.resultTime = TimeUtil.nowMs();
                App.preferences().edit().putLong("prevUpdateTime", resultTime).apply();
                App.preferences().edit().putString("current_weather_condition", weather.getDescription()).apply();
                App.preferences().edit().putString("current_weather_update_time", Util.epochToString(resultTime)).apply();
                L.v("successful result: " + result.toString());
                afterResult(Result.OKAY, result);
            } else {
                L.v("failed");
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
        EventBus.getDefault().unregister(this);
    }
}
