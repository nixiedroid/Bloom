/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.shadow;

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

public abstract class ShadowWallpaperService
extends UtWallpaperService {
    private static ShadowWallpaperService instance;
    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context object, Intent intent) {
            boolean bl = ContextCompat.checkSelfPermission(ShadowWallpaperService.this, "android.permission.ACCESS_FINE_LOCATION") == 0;
            L.v("got this - has permission? " + bl);
            if (bl) {
                ShadowWallpaperService.this.weatherMan.start();
                ShadowWallpaperService.this.sunriseUtil.get();
            }
        }
    };
    private SunriseUtil sunriseUtil;
    private WeatherManager weatherMan;

    public static ShadowWallpaperService get() {
        return instance;
    }

    private void initPermissionsRelated(UtRenderer object) {
        if (!((UtRenderer)object).isPreview()) {
            return;
        }
        LocalBroadcastManager.getInstance(App.get()).registerReceiver(this.permissionReceiver, new IntentFilter("permission_result"));
        boolean bl = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0;
        L.d("has fine location permissions? " + bl);
        if (!bl) {
            PermissionsActivity.launchActivity(this, 1);
        }
    }

    public abstract Config config();

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
        return new ShadowRenderer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d();
        instance = this;
        ShadowColors.init();
        Configs.init();
        weatherMan = new WeatherManager(getApplicationContext());
        sunriseUtil = new SunriseUtil();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        weatherMan.stop();
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(permissionReceiver);
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

