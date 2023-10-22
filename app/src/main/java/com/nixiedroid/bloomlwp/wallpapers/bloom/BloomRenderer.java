package com.nixiedroid.bloomlwp.wallpapers.bloom;

import android.app.WallpaperColors;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Build;
import android.view.animation.PathInterpolator;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.AnimFloat;
import com.nixiedroid.bloomlwp.util.ColUtil;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.util.Terps;
import com.nixiedroid.bloomlwp.wallpapers.base.UtRenderer;
import com.nixiedroid.bloomlwp.wallpapers.weather.TimeUtil;
import com.nixiedroid.bloomlwp.wallpapers.weather.WeatherVo;
import com.nixiedroid.bloomlwp.wallpapers.weather.owm.SunriseUtil;
import com.nixiedroid.bloomlwp.wallpapers.weather.owm.WeatherManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BloomRenderer
extends UtRenderer {
    private static final int[] weatherConditionByPriority = new int[]{8, 6, 7, 2, 3, 4, 5, 9, 1, 0};
    private Gradient gradient;
    private GradientSetManager gradientMan;
    private boolean isOscillationDisabled;
    private Gradient lastGradient;
    private int lastWeatherCondition;
    private BloomProgram program;
    private Gradient scratch1 = new Gradient();
    private Gradient scratch2 = new Gradient();
    private int startSlidingIndex;
    private long startTime;
    private BroadcastReceiver sunriseReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context object, Intent intent) {
            final boolean booleanExtra = intent.getBooleanExtra("sunrise_result", false);
            L.v("got sunrise broadcast - " +booleanExtra);
            BloomRenderer.this.gradientMan.updateAllRanges();
        }
    };
    private AnimFloat unlockBottomFadeInAnim;
    private AnimFloat unlockBottomFadeoutAnim;
    private AnimFloat unlockBottomSlideAnim;
    private AnimFloat unlockTopFadeInAnim;
    private AnimFloat unlockTopFadeoutAnim;
    private AnimFloat unlockTopSlideAnim;
    private final BroadcastReceiver weatherBroadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("weather_result", false)) {
                BloomRenderer.this.updateWeatherCondition();
            }
        }
    };
    private int weatherCondition;
    private float weatherTransitionPercent;

    public BloomRenderer() {
        if (App.get().bloomTestSettings != null) {
            if (App.get().bloomTestSettings.shouldDisableOscillation) {
                this.isOscillationDisabled = true;
            }
        }
        TimeUtil.setAccelerated(false);
        this.updateWeatherCondition();
        this.lastWeatherCondition = this.weatherCondition;
        this.gradient = new Gradient();
        this.lastGradient = new Gradient();
        this.gradientMan = new GradientSetManager();
        this.initUnlockAnims();
        IntentFilter intentFilter = new IntentFilter("sunrise_result");
        LocalBroadcastManager.getInstance(App.get()).registerReceiver(this.sunriseReceiver, intentFilter);

        intentFilter = new IntentFilter("weather_result");
        LocalBroadcastManager.getInstance(App.get()).registerReceiver(this.weatherBroadcastReceiver, intentFilter);
        this.startTime = System.currentTimeMillis();
        this.startSlidingIndex = (int)this.gradientMan.gradientSetByCondition(this.weatherCondition).calcSlidingIndex(TimeUtil.nowDayPercent());
    }

    private void initUnlockAnims() {
        this.unlockTopFadeoutAnim = new AnimFloat(1.0f, 0.0f, 1200L, 0L, Terps.linear());
        this.unlockBottomFadeoutAnim = new AnimFloat(1.0f, 0.0f, 1200L, 0L, Terps.linear());
        PathInterpolator pathInterpolator = new PathInterpolator(0.46f, 0.65f, 0.41f, 0.98f);
        this.unlockTopSlideAnim = new AnimFloat(0.0f, 1.0f, 3350L, 0L, pathInterpolator);
        this.unlockBottomSlideAnim = new AnimFloat(0.0f, 1.0f, 3350L, 0L, pathInterpolator);
        this.unlockTopFadeInAnim = new AnimFloat(0.15f, 1.0f, 1000L, Terps.linear());
        this.unlockBottomFadeInAnim = new AnimFloat(0.0f, 1.0f, 1750L, Terps.accelerate05());
        this.unlockTopFadeoutAnim.setToEnd();
        this.unlockBottomFadeoutAnim.setToEnd();
        this.unlockTopSlideAnim.setToEnd();
        this.unlockBottomSlideAnim.setToEnd();
        this.unlockTopFadeInAnim.setToEnd();
        this.unlockBottomFadeInAnim.setToEnd();
    }

    private void updateGradient() {
        Gradient.copyFromTo(this.gradient, this.lastGradient);
        long l = TimeUtil.elapsedRealTimeSince(BloomWallpaperService.get().weatherMan().resultTime());
        float f = this.weatherTransitionPercent;
        this.weatherTransitionPercent = MathUtil.normalize(l, 0.0f, 3000.0f, true);
        float f2 = TimeUtil.nowDayPercent();
        float f3 = this.weatherTransitionPercent;
        if (f3 < 1.0f) {
            this.gradientMan.lerpUsingDayPercent(this.lastWeatherCondition, this.weatherCondition, f3, f2, this.isOscillationDisabled ^ true, this.gradient);
        } else {
            this.gradientMan.gradientSetByCondition(this.weatherCondition).lerpUsingDayPercent(f2, true ^ this.isOscillationDisabled, this.gradient);
            if (this.weatherTransitionPercent != f) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    this.engine.notifyColorsChanged();
                }
            }
        }
    }

    private void updateGradientDebugMode() {
        Gradient.copyFromTo(this.gradient, this.lastGradient);
        this.weatherTransitionPercent = MathUtil.map(System.currentTimeMillis() - this.surfaceChangeTime, 2000.0f, 5000.0f, 0.0f, 1.0f, true);
        BloomTestSettings bloomTestSettings = App.get().bloomTestSettings;
        if (bloomTestSettings.timeIndex2 <= -1 && bloomTestSettings.weather2 <= -1) {
            this.gradientMan.calcGradientByConditionAndTimeIndexWithOscillation(bloomTestSettings.weather1, bloomTestSettings.timeIndex1, this.isOscillationDisabled, this.gradient);
        } else {
            this.gradientMan.calcGradientByConditionAndTimeIndexWithOscillation(bloomTestSettings.weather1, bloomTestSettings.timeIndex1, this.isOscillationDisabled, this.scratch1);
            int n = bloomTestSettings.timeIndex2;
            if (n > -1) {
                this.gradientMan.calcGradientByConditionAndTimeIndexWithOscillation(bloomTestSettings.weather1, n, this.isOscillationDisabled, this.scratch2);
            } else {
                n = bloomTestSettings.weather2;
                if (n > -1) {
                    this.gradientMan.calcGradientByConditionAndTimeIndexWithOscillation(n, bloomTestSettings.timeIndex1, this.isOscillationDisabled, this.scratch2);
                }
            }
            Gradient.lerp(this.scratch1, this.scratch2, this.weatherTransitionPercent, this.gradient);
        }
    }

    private void updateGradientPreviewMode() {
        Gradient.copyFromTo(this.gradient, this.lastGradient);
        this.weatherTransitionPercent = MathUtil.normalize(TimeUtil.elapsedRealTimeSince(BloomWallpaperService.get().weatherMan().resultTime()), 0.0f, 3000.0f, true);
        float f = (float)(System.currentTimeMillis() - this.startTime) / 1000.0f / 30.0f % 1.0f / 0.16666667f;
        int n = (int)Math.floor(f);
        f = MathUtil.map(f % 1.0f, 0.166f, 0.833f, 0.0f, 1.0f, true);
        f = ((float)(n % 6) + f + (float)this.startSlidingIndex) % 6.0f;
        float f2 = this.weatherTransitionPercent;
        if (f2 < 1.0f) {
            this.gradientMan.lerpUsingSlidingIndex(this.lastWeatherCondition, this.weatherCondition, f2, f, this.isOscillationDisabled ^ true, this.gradient);
        } else {
            this.gradientMan.gradientSetByCondition(this.weatherCondition).lerpUsingSlidingIndex(f, true ^ this.isOscillationDisabled, this.gradient);
        }
    }

    private void updateWeatherCondition() {
        int n;
        int n2;
        this.lastWeatherCondition = this.weatherCondition;
        int[] nArray = BloomWallpaperService.get().weatherMan().result().conditions;
        int[] nArray2 = new int[nArray.length];
        int n3 = 0;
        for (n2 = 0; n2 < nArray.length; ++n2) {
            int[] nArray3;
            for (n = 0; n < (nArray3 = weatherConditionByPriority).length; ++n) {
                if (nArray3[n] != nArray[n2]) continue;
                nArray2[n2] = n;
            }
        }
        int n4 = Integer.MAX_VALUE;
        n = -1;
        for (n2 = n3; n2 < nArray2.length; ++n2) {
            n3 = n4;
            if (nArray2[n2] < n4) {
                n3 = nArray2[n2];
                n = n2;
            }
            n4 = n3;
        }
        this.weatherCondition = nArray[n];
        L.v(WeatherVo.conditionString(this.weatherCondition));
    }

    @Override
    protected void doDraw() {
        int width;
        int height;
        if (this.unlockTopSlideAnimValue() < 1.0f) {
            height = this.viewportHeight;
            width = 0;
        } else {
            width = (int)((float)this.viewportHeight * 0.2f);
            height = (int)((float)this.viewportHeight * 0.55f);
        }
        GLES20.glClearColor(this.gradient().middle[0], this.gradient().middle[1], this.gradient().middle[2], 1.0f);
        GLES20.glEnable(3089);
        GLES20.glScissor(0, this.viewportHeight - height, this.viewportWidth, height - width);
        GLES20.glClear(16384);
        GLES20.glDisable(3089);
        GLES20.glDisable(3024);
    }

    @Override
    protected void doUpdate() {
        if (App.get().bloomTestSettings != null && App.get().bloomTestSettings.useCustomWeather) {
            this.updateGradientDebugMode();
        } else if (this.isPreview) {
            this.updateGradientPreviewMode();
        } else {
            this.updateGradient();
        }
    }

    @Override
    public int frameInterval() {
        int interval = 2;
        if (this.program != null && !this.unlockTopSlideAnim.isRunning() && !this.unlockTopFadeoutAnim.isRunning()) {
            if (!this.isPreview && !(this.weatherTransitionPercent < 1.0f)) {
                interval = 10;
            }
        } else {
            interval = 1;
        }
        return interval;
    }

    public Gradient gradient() {
        return this.gradient;
    }

    @Override
    public WallpaperColors onComputeWallpaperColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new WallpaperColors(
                    Color.valueOf(ColUtil.rgbToInt(this.gradient.lower2)),
                    Color.valueOf(ColUtil.rgbToInt(this.gradient.lower1)),
                    Color.valueOf(ColUtil.rgbToInt(this.gradient.middle)), 0);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                return new WallpaperColors(Color.valueOf(ColUtil.rgbToInt(this.gradient.lower2)),
                        Color.valueOf(ColUtil.rgbToInt(this.gradient.lower1)),
                        Color.valueOf(ColUtil.rgbToInt(this.gradient.middle)));
            }
        }
       return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this.sunriseReceiver);
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this.weatherBroadcastReceiver);
    }

    @Override
    protected void onNotVisible() {
        super.onNotVisible();
        WeatherManager man = BloomWallpaperService.get().weatherMan();
        if (man != null) man.stop();
    }

    @Override
    public void onSurfaceChanged(GL10 gL10, int n, int n2) {
        super.onSurfaceChanged(gL10, n, n2);
        GLES20.glViewport(0, 0, n, n2);
        this.program.onViewportSizeChanged();
    }

    @Override
    public void onSurfaceCreated(GL10 gL10, EGLConfig eGLConfig) {
        super.onSurfaceCreated(gL10, eGLConfig);
        this.program = new BloomProgram(this);
        this.addChildNode(this.program);
    }

    @Override
    protected void onUserPresent() {
        super.onUserPresent();
        if (this.isPreview()) {
            return;
        }
        this.unlockTopFadeoutAnim.start();
        this.unlockTopSlideAnim.start();
        this.unlockBottomFadeoutAnim.start();
        this.unlockBottomSlideAnim.start();
        this.unlockTopFadeInAnim.start();
        this.unlockBottomFadeInAnim.start();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        WeatherManager man = BloomWallpaperService.get().weatherMan();
        if (man != null) man.start();
        SunriseUtil util = BloomWallpaperService.get().sunriseUtil();
        if (util != null) util.get();
        BloomProgram bloomProgram = this.program;
        if (bloomProgram != null) {
            bloomProgram.onVisible();
        }
        this.schedulerRequestNow();
    }

    @Override
    protected void onVisibleAtLockScreen() {
        super.onVisibleAtLockScreen();
        BloomProgram bloomProgram = this.program;
        if (bloomProgram != null) {
            bloomProgram.onVisibleAtLockScreen();
        }
        this.schedulerRequestNow();
        WeatherManager man = BloomWallpaperService.get().weatherMan();
        if (man != null) man.start();
        SunriseUtil util = BloomWallpaperService.get().sunriseUtil();
        if (util != null) util.get();
    }

    public float unlockBottomFadeInAnimValue() {
        return this.unlockBottomFadeInAnim.value();
    }

    public float unlockBottomFadeoutAnimValue() {
        return this.unlockBottomFadeoutAnim.value();
    }

    public float unlockBottomSlideAnimValue() {
        return this.unlockBottomSlideAnim.value();
    }

    public float unlockTopFadeInAnimValue() {
        return this.unlockTopFadeInAnim.value();
    }

    public float unlockTopFadeoutAnimValue() {
        return this.unlockTopFadeoutAnim.value();
    }

    public float unlockTopSlideAnimValue() {
        return this.unlockTopSlideAnim.value();
    }
}

