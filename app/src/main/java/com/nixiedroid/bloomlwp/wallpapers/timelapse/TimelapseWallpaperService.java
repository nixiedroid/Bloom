package com.nixiedroid.bloomlwp.wallpapers.timelapse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.PermissionsActivity;
import com.nixiedroid.bloomlwp.wallpapers.base.UtRenderer;
import com.nixiedroid.bloomlwp.wallpapers.base.UtWallpaperService;
import com.nixiedroid.bloomlwp.wallpapers.util.owm.SunriseUtil;
import com.nixiedroid.bloomlwp.wallpapers.util.owm.WeatherManager;

public class TimelapseWallpaperService
extends UtWallpaperService {
    private static TimelapseWallpaperService instance;
    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context object, Intent intent) {
            boolean bl = ContextCompat.checkSelfPermission(TimelapseWallpaperService.this, "android.permission.ACCESS_FINE_LOCATION") == 0;
            L.v("got this - has permission? " );
            if (bl) {
                TimelapseWallpaperService.this.weatherMan.start();
                TimelapseWallpaperService.this.sunriseUtil.get();
            }
        }
    };
    private SunriseUtil sunriseUtil;
    private WeatherManager weatherMan;

    public static TimelapseWallpaperService get() {
        return instance;
    }

    private void initPermissionsRelated(UtRenderer object) {
        if (!object.isPreview()) {
            return;
        }
        LocalBroadcastManager.getInstance(
                App.get()).registerReceiver(this.permissionReceiver, new IntentFilter("permission_result")
        );
        boolean bl = ContextCompat.checkSelfPermission(
                this, "android.permission.ACCESS_FINE_LOCATION") == 0;
        L.d("has fine location permissions? " + bl);
        if (!bl) {
            PermissionsActivity.launchActivity(this, 2);
        }
    }

    @Override
    protected boolean handlesGravity() {
        return true;
    }

    @Override
    protected boolean handlesTouchesAndGestures() {
        return true;
    }

    @Override
    protected UtRenderer makeRenderer() {
        return new TimelapseRenderer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d();
        instance = this;
        this.weatherMan = new WeatherManager(this.getApplicationContext());
        this.sunriseUtil = new SunriseUtil();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.weatherMan.stop();
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this.permissionReceiver);
        instance = null;
    }

    @Override
    protected void onWallpaperAttached(UtRenderer utRenderer) {
        this.initPermissionsRelated(utRenderer);
    }

    public SunriseUtil sunriseUtil() {
        return this.sunriseUtil;
    }

    public WeatherManager weatherMan() {
        return this.weatherMan;
    }
}

