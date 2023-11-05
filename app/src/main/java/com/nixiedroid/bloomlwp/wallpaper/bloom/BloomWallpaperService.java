package com.nixiedroid.bloomlwp.wallpaper.bloom;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpaper.base.Renderer;
import com.nixiedroid.bloomlwp.wallpaper.base.WallpaperService;
import com.nixiedroid.bloomlwp.weather.SunriseUtil;
import com.nixiedroid.bloomlwp.weather.owm.WeatherManager;

public class BloomWallpaperService
extends WallpaperService {
    private static BloomWallpaperService instance;
    private final BroadcastReceiver permissionReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context object, Intent intent) {
            L.v("got this - has permission? " );
            if (ContextCompat.checkSelfPermission(BloomWallpaperService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == 0) {
                weatherMan.start();
                sunriseUtil.get();
            }
        }
    };
    private SunriseUtil sunriseUtil;
    private WeatherManager weatherMan;

    public static BloomWallpaperService get() {
        return instance;
    }

    private void initPermissionsRelated(Renderer renderer) {
        if (!renderer.isPreview()) {
            LocalBroadcastManager.getInstance(App.get()).registerReceiver(this.permissionReceiver, new IntentFilter("permission_result"));
            boolean hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == 0;
            L.d("has fine location permissions? " + hasPermission);
            if (!hasPermission) {
                Toast.makeText(App.get(),"No location permission granted. \nPlease, grant it in settings", Toast.LENGTH_LONG).show();
            }
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
    protected Renderer makeRenderer() {
        return new BloomRenderer();
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
        this.weatherMan.destroy();
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this.permissionReceiver);
        instance = null;
    }

    @Override
    protected void onWallpaperAttached(Renderer renderer) {
        this.initPermissionsRelated(renderer);
    }

    public SunriseUtil sunriseUtil() {
        return this.sunriseUtil;
    }

    public WeatherManager weatherMan() {
        return this.weatherMan;
    }
}

