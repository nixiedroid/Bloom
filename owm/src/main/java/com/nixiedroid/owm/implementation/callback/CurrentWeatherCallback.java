package com.nixiedroid.owm.implementation.callback;

import com.nixiedroid.owm.model.currentweather.CurrentWeather;

public interface CurrentWeatherCallback{
    void onSuccess(CurrentWeather currentWeather);
    void onFailure(Throwable throwable);
}
