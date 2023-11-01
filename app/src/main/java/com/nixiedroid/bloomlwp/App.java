package com.nixiedroid.bloomlwp;

import android.app.Application;
import android.content.SharedPreferences;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.bloom.BloomTestSettings;
import com.nixiedroid.bloomlwp.wallpapers.weather.TimeUtil;

public class App
extends Application {
    private static App instance;
    private static SharedPreferences preferences;
    public BloomTestSettings bloomTestSettings;

    public static App get() {
        return instance;
    }
    public static SharedPreferences preferences(){
        return preferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferences = createDeviceProtectedStorageContext().getSharedPreferences("cache", 0);
        L.d("\n----------------------------------------------------------------------------------");
        TimeUtil.init();
        FlavorTypeOverride.afterCreate();
    }
}

