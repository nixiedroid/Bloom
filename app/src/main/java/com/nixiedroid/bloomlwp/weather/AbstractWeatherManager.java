package com.nixiedroid.bloomlwp.weather;

import android.os.Handler;
import android.os.Looper;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.events.WeatherResult;
import com.nixiedroid.bloomlwp.util.L;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public abstract class AbstractWeatherManager {
    protected boolean isStopped;
    protected Handler pollHandler;
    protected WeatherVo result;
    protected long resultTime;

    protected Runnable runnable;

    public AbstractWeatherManager() {
        isStopped = false;
        runnable = AbstractWeatherManager.this::get;
        pollHandler = new Handler(Looper.getMainLooper());
        String defaultResults = "1";
        result = this.stringToWeather(App.preferences().getString("weather_condition", defaultResults));
        resultTime = TimeUtil.nowMs();
    }


    private WeatherVo stringToWeather(String result) {
        ArrayList<Integer> resultInteger = new ArrayList<>(1);
        try {
            resultInteger.add(Integer.parseInt(result));
        } catch (NumberFormatException ignored) {
        }

        WeatherVo weatherVo = new WeatherVo();
        if (!resultInteger.isEmpty()) {
            weatherVo.conditions = new int[resultInteger.size()];
            for (int i = 0; i < resultInteger.size(); i++) {
                weatherVo.conditions[i] = resultInteger.get(i);
            }
        } else {
            weatherVo.conditions = new int[]{1};
        }
        return weatherVo;
    }

    private String weatherToString(WeatherVo weatherVo) {
        String result = "";
        for (int i = 0; i < weatherVo.conditions.length; ++i) {
            result = Integer.toString(weatherVo.conditions[i]);
        }
        return result;
    }

    protected void afterResult(Result result) {
        this.afterResult(result, null);
    }

    protected void afterResult(Result result, WeatherVo weatherVo) {
        EventBus.getDefault().post(new WeatherResult(result));
        pollHandler.removeCallbacks(runnable);
        pollHandler.postDelayed(runnable, intervalMs());
        if (result == Result.OKAY && weatherVo != null) {
            App.preferences().edit().putString("weather_condition", this.weatherToString(weatherVo)).apply();
        }
    }

    protected abstract void doGet();

    protected void get() {
        if (this.isStopped) {
            L.v("is stopped; ignoring");
            return;
        }
        this.doGet();
    }

    protected int intervalMs() {
        return 900000;
    }

    public WeatherVo result() {
        return this.result;
    }

    public long resultTime() {
        return this.resultTime;
    }

    public void start() {
        this.isStopped = false;
        this.get();
    }

    public void destroy() {
        this.isStopped = true;
    }

    public void stop() {
        this.isStopped = true;
    }


}

