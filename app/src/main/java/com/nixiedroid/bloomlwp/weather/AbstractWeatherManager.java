package com.nixiedroid.bloomlwp.weather;

import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.events.WeatherResult;
import com.nixiedroid.bloomlwp.util.L;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Set;

public abstract class AbstractWeatherManager {
    protected boolean isStopped;
    protected Handler pollHandler;
    protected WeatherVo previousResult;
    protected WeatherVo result;
    protected long resultTime;

    protected Runnable runnable;

    public AbstractWeatherManager() {
        isStopped = false;
        runnable = AbstractWeatherManager.this::get;
        pollHandler = new Handler(Looper.getMainLooper());
        ArraySet<String> defaultResults = new ArraySet<>();
        defaultResults.add("1");
        result = this.stringSetToWeather(  App.preferences().getStringSet("weather_condition", defaultResults));
        resultTime = TimeUtil.nowMs();
        previousResult = this.result;
    }


    private WeatherVo stringSetToWeather(Set<String> results) {
        ArrayList<Integer> resultInteger = new ArrayList<>(results.size());
        for (String result : results) {
            try {
                resultInteger.add(Integer.parseInt(result));
            } catch (NumberFormatException ignored) {
            }
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

    private Set<String> weatherToStringSet(WeatherVo weatherVo) {
        ArraySet<String> arraySet = new ArraySet<>();
        for (int i = 0; i < weatherVo.conditions.length; ++i) {
            arraySet.add(Integer.toString(weatherVo.conditions[i]));
        }
        return arraySet;
    }

    protected void afterResult(Result result) {
        this.afterResult(result, null);
    }

    protected void afterResult(Result result, WeatherVo weatherVo) {
        EventBus.getDefault().post(new WeatherResult(result == Result.OKAY));
        pollHandler.removeCallbacks(runnable);
        pollHandler.postDelayed(runnable, intervalMs());
        if (result == Result.OKAY && weatherVo != null) {
           App.preferences().edit().putStringSet("weather_condition", this.weatherToStringSet(weatherVo)).apply();
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

    public enum Result {
        OKAY,
        FAILED,
        FAILED_NO_PERMISSION,
        STOPPED

    }
}

