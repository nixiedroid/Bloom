package com.nixiedroid.bloomlwp.wallpaper.bloom;

import android.Manifest;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpaper.base.Renderer;
import com.nixiedroid.bloomlwp.wallpaper.base.WallpaperService;
import com.nixiedroid.bloomlwp.weather.SunriseUtil;
import com.nixiedroid.bloomlwp.weather.owm.WeatherManager;

public class BloomWallpaperService
        extends WallpaperService {
    private static BloomWallpaperService instance;

    private SunriseUtil sunriseUtil;
    private WeatherManager weatherMan;

    public static BloomWallpaperService get() {
        return instance;
    }

    private void initPermissionsRelated(Renderer renderer) {
        if (!renderer.isPreview()) {
            boolean hasPermission =
                    ContextCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION) == 0;
            L.d("has fine location permissions? " + hasPermission);
            if (!hasPermission) {
                Toast.makeText(
                        App.get(),
                        R.string.no_permission_warning,
                        Toast.LENGTH_LONG
                ).show();
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
        this.weatherMan = new WeatherManager();
        this.sunriseUtil = new SunriseUtil();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.weatherMan.destroy();
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

