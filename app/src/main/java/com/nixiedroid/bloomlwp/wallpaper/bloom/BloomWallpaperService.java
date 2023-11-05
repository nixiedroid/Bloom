package com.nixiedroid.bloomlwp.wallpaper.bloom;

import android.Manifest;
import android.os.Build;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpaper.base.Renderer;
import com.nixiedroid.bloomlwp.wallpaper.base.WallpaperService;
import com.nixiedroid.bloomlwp.weather.SunriseUtil;
import com.nixiedroid.bloomlwp.weather.owm.WeatherManager;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

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
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PermissionChecker.PERMISSION_GRANTED;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                hasPermission |=  ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PermissionChecker.PERMISSION_GRANTED;
            }
            boolean isGoogleAvailable = false;
            try {
                int checkResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(App.get());
                isGoogleAvailable = (checkResult == SUCCESS);
            } catch (NoClassDefFoundError ignored) {
            }
            if (!isGoogleAvailable) {
                hasPermission |=  ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PermissionChecker.PERMISSION_GRANTED;
            }
            L.d("has fine location permissions? " + hasPermission);
            if (!hasPermission) {
                Toast.makeText(
                        App.get(),
                        R.string.no_permission_warning,
                        Toast.LENGTH_LONG
                ).show();
            } else {
                weatherMan.start();
                sunriseUtil.get();
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

