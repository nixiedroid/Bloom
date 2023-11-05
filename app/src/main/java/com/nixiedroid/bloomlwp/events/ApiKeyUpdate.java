package com.nixiedroid.bloomlwp.events;

public class ApiKeyUpdate {
    private final String apiKey;

    public ApiKeyUpdate(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

}
