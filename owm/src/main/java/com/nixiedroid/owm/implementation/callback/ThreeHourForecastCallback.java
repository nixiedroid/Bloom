package com.nixiedroid.owm.implementation.callback;

import com.nixiedroid.owm.model.threehourforecast.ThreeHourForecast;

public interface ThreeHourForecastCallback{
    void onSuccess(ThreeHourForecast threeHourForecast);
    void onFailure(Throwable throwable);
}