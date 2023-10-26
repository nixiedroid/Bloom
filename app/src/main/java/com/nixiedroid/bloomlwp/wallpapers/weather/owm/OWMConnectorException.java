package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

import com.nixiedroid.bloomlwp.wallpapers.weather.owm.enums.ErrorResult;

public class OWMConnectorException extends Exception{
    public OWMConnectorException(ErrorResult errorResult){
        super(errorResult.getMessage());
    }

    public OWMConnectorException(String message) {
        super(message);
    }
}
