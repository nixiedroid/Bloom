package com.nixiedroid.bloomlwp;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Debug;
import androidx.appcompat.app.AppCompatDelegate;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.weather.TimeUtil;
import org.greenrobot.eventbus.EventBus;

public class App
extends Application {
    private static App instance;
    private static SharedPreferences preferences;

    public static App get() {
        return instance;
    }
    public static SharedPreferences preferences(){
        return preferences;
    }

    @Override
    public void onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        EventBus.builder().addIndex(new EventBusIndex()).build();
        EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();
        super.onCreate();

        instance = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            preferences = createDeviceProtectedStorageContext().getSharedPreferences("cache", 0);
        } else {
            preferences = getSharedPreferences("cache", 0);
        }
        L.d("\n----------------------------------------------------------------------------------");
        TimeUtil.init();
    }
}

