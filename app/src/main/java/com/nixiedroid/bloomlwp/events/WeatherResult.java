package com.nixiedroid.bloomlwp.events;

public class WeatherResult {
    private final boolean isOkay;

    public WeatherResult(boolean isOkay) {
        this.isOkay = isOkay;
    }

    public boolean isOkay() {
        return isOkay;
    }
}
