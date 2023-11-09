package com.nixiedroid.bloomlwp.wallpaper.bloom;

import android.app.WallpaperColors;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Build;
import android.view.animation.Interpolator;
import androidx.core.view.animation.PathInterpolatorCompat;
import com.nixiedroid.bloomlwp.events.SunriseResult;
import com.nixiedroid.bloomlwp.events.WeatherResult;
import com.nixiedroid.bloomlwp.util.*;
import com.nixiedroid.bloomlwp.wallpaper.base.Renderer;
import com.nixiedroid.bloomlwp.weather.Result;
import com.nixiedroid.bloomlwp.weather.SunriseUtil;
import com.nixiedroid.bloomlwp.weather.TimeUtil;
import com.nixiedroid.bloomlwp.weather.WeatherVo;
import com.nixiedroid.bloomlwp.weather.owm.WeatherManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BloomRenderer
        extends Renderer {
    private static final int[] weatherConditionByPriority = new int[]{8, 6, 7, 2, 3, 4, 5, 9, 1, 0};
    private final Gradient gradient;
    private final GradientSetManager gradientMan;
    private final Gradient lastGradient;
    private final int startSlidingIndex;
    private final long startTime;
    private int lastWeatherCondition;
    private BloomProgram program;
    private AnimFloat unlockBottomFadeInAnim;
    private AnimFloat unlockBottomFadeoutAnim;
    private AnimFloat unlockBottomSlideAnim;
    private AnimFloat unlockTopFadeInAnim;
    private AnimFloat unlockTopFadeoutAnim;
    private AnimFloat unlockTopSlideAnim;
    private int weatherCondition;
    private float weatherTransitionPercent;

    public BloomRenderer() {
        EventBus.getDefault().register(this);
        this.updateWeatherCondition();
        this.lastWeatherCondition = this.weatherCondition;
        this.gradient = new Gradient();
        this.lastGradient = new Gradient();
        this.gradientMan = new GradientSetManager();
        this.initUnlockAnims();
        this.startTime = System.currentTimeMillis();
        this.startSlidingIndex = (int) gradientMan.gradientSetByCondition(this.weatherCondition).calcSlidingIndex(TimeUtil.nowDayPercent());
    }

    /**
     * @noinspection unused
     */
    @Subscribe
    public void weatherReceiver(WeatherResult event) {
        L.v("got weather broadcast - " + event.getResult());
        if (event.getResult() == Result.OKAY) {
            BloomRenderer.this.updateWeatherCondition();
        }
    }

    /**
     * @noinspection unused
     */
    @Subscribe
    public void sunriseReceiver(SunriseResult event) {
        L.v("got sunrise broadcast - ");
        BloomRenderer.this.gradientMan.updateAllRanges();
    }

    private void initUnlockAnims() {
        this.unlockTopFadeoutAnim = new AnimFloat(1.0f, 0.0f, 1200L, 0L, Terps.linear());
        this.unlockBottomFadeoutAnim = new AnimFloat(1.0f, 0.0f, 1200L, 0L, Terps.linear());
        Interpolator pathInterpolator = PathInterpolatorCompat.create(0.46f, 0.65f, 0.41f, 0.98f);
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
        long elapsedTimeSinceLastWeatherUpdate = TimeUtil.elapsedRealTimeSince(BloomWallpaperService.get().weatherMan().resultTime());
        float prevWTP = this.weatherTransitionPercent;
        this.weatherTransitionPercent = MathUtil.normalize(elapsedTimeSinceLastWeatherUpdate, 0.0f, 3000.0f, true);
        float dayPercent = TimeUtil.nowDayPercent();
        if (this.weatherTransitionPercent < 1.0f) {
            this.gradientMan.lerpUsingDayPercent(this.lastWeatherCondition, this.weatherCondition, this.weatherTransitionPercent, dayPercent, gradient);
        } else {
            this.gradientMan.gradientSetByCondition(this.weatherCondition).lerpUsingDayPercent(dayPercent, gradient);
            if (this.weatherTransitionPercent != prevWTP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    this.engine.notifyColorsChanged();
                }
            }
        }
    }


    private void updateGradientPreviewMode() {
        Gradient.copyFromTo(this.gradient, this.lastGradient);
        this.weatherTransitionPercent = MathUtil.normalize(TimeUtil.elapsedRealTimeSince(BloomWallpaperService.get().weatherMan().resultTime()), 0.0f, 3000.0f, true);
        float f = (float) (System.currentTimeMillis() - this.startTime) / 1000.0f / 30.0f % 1.0f / 0.16666667f;
        int n = (int) Math.floor(f);
        f = MathUtil.map(f % 1.0f, 0.166f, 0.833f, 0.0f, 1.0f, true);
        f = ((float) (n % 6) + f + (float) this.startSlidingIndex) % 6.0f;
        float f2 = this.weatherTransitionPercent;
        if (f2 < 1.0f) {
            this.gradientMan.lerpUsingSlidingIndex(this.lastWeatherCondition, this.weatherCondition, f2, f, this.gradient);
        } else {
            this.gradientMan.gradientSetByCondition(this.weatherCondition).lerpUsingSlidingIndex(f, this.gradient);
        }
    }

    private void updateWeatherCondition() {

        this.lastWeatherCondition = this.weatherCondition;
        int[] weatherConditions = BloomWallpaperService.get().weatherMan().result().conditions;
        int[] priorities = new int[weatherConditions.length];
        for (int i = 0; i < weatherConditions.length; ++i) {
            for (int k = 0; k < weatherConditionByPriority.length; ++k) {
                if (weatherConditionByPriority[k] == weatherConditions[i]) {
                    priorities[i] = k;
                }
            }
        }
        int maxValue = Integer.MAX_VALUE;
        int prevMaxValue = Integer.MAX_VALUE;
        int resultIndex = -1;
        for (int i = 0; i < priorities.length; ++i) {
            if (priorities[i] < prevMaxValue) {
                maxValue = priorities[i];
                resultIndex = i;
            }
            prevMaxValue = maxValue;
        }
        this.weatherCondition = weatherConditions[resultIndex];
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
            width = (int) ((float) this.viewportHeight * 0.2f);
            height = (int) ((float) this.viewportHeight * 0.55f);
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
        if (this.isPreview) {
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
    public Object onComputeWallpaperColors() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                int hints =0;
                if (ColUtil.isWhite(this.gradient.middle)) hints = WallpaperColors.HINT_SUPPORTS_DARK_TEXT;
                return new WallpaperColors(
                        Color.valueOf(ColUtil.rgbToInt(this.gradient.upper1)),
                        Color.valueOf(ColUtil.rgbToInt(this.gradient.lower1)),
                        Color.valueOf(ColUtil.rgbToInt(this.gradient.middle)), hints);
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
        WeatherManager man = BloomWallpaperService.get().weatherMan();
        if (man != null) man.start();
        SunriseUtil util = BloomWallpaperService.get().sunriseUtil();
        if (util != null) util.get();
        BloomProgram bloomProgram = this.program;
        if (bloomProgram != null) {
            bloomProgram.onVisibleAtLockScreen();
        }
        this.schedulerRequestNow();
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

