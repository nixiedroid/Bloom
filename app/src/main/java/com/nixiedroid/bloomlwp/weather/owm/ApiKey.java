package com.nixiedroid.bloomlwp.weather.owm;

public class ApiKey {
    public static void validateAPIKey(String apiKey) throws IllegalArgumentException {
        if (apiKey.length() != 32) throw new IllegalArgumentException("Invalid length");
    }
}
