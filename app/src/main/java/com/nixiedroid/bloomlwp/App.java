package com.nixiedroid.bloomlwp;

import android.app.Application;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.timelapse.TimelapseTestSettings;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;

public class App
extends Application {
    private static App instance;
    public TimelapseTestSettings timelapseTestSettings;

    public static App get() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        L.d("\n----------------------------------------------------------------------------------");
        TimeUtil.init(this);
        FlavorTypeOverride.afterCreate();
    }
}

