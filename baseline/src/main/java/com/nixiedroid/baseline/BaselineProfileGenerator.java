package com.nixiedroid.baseline;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import androidx.benchmark.macro.junit4.BaselineProfileRule;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.junit.Rule;
import org.junit.Test;

public class BaselineProfileGenerator {

    @Rule
    public BaselineProfileRule baselineRule = new BaselineProfileRule();

    @Test
    public void startupBaselineProfile() {
        baselineRule.collect(
                "com.nixiedroid.bloom",
                (scope -> {
                    scope.startActivityAndWait(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
    ComponentName.createRelative("com.nixiedroid.bloomlwp.wallpapers.bloom","BloomWallpaperService"));
                            return Unit.INSTANCE;
                        }
                    });
                    return Unit.INSTANCE;
                })
        );
    }
}