package com.nixiedroid.bloomlwp.wallpapers.orbit;

import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.wallpapers.base.UtRenderer;
import com.nixiedroid.bloomlwp.wallpapers.base.UtWallpaperService;

public abstract class OrbitWallpaperService
extends UtWallpaperService {
    private static OrbitWallpaperService instance;

    public static OrbitWallpaperService get() {
        return instance;
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
        L.d();
        return new OrbitRenderer();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Configs.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

