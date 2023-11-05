package com.nixiedroid.bloomlwp.events;

import com.nixiedroid.bloomlwp.weather.Result;

public class WeatherResult {
    private final Result result;

    public WeatherResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }
}
