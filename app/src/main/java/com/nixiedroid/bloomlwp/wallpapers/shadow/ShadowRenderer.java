/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.shadow;

import android.app.WallpaperColors;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.util.Vec3f;
import com.nixiedroid.bloomlwp.wallpapers.Constants;
import com.nixiedroid.bloomlwp.wallpapers.base.UtRenderer;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShadowRenderer
extends UtRenderer {
    static final Vec3f RESTING_GRAVITY = new Vec3f(0.0f, 0.66f, 1.0f);
    private PointF downPoint;
    private long downTime;
    private PointF dragPoint;
    private float flingFactor;
    private boolean flingFlag;
    private float gravityAccumulator;
    private boolean isOscillationDisabled;
    private boolean isResting;
    private long lastShadowPulseTimeCheck;
    private ShadowProgram program;
    private long shadowPulseTime;
    private long upTime;
    private BroadcastReceiver weatherBroadcastReceiver;

    public ShadowRenderer() {
        boolean bl = true;
        this.isResting = true;
        this.weatherBroadcastReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean bl = intent.getBooleanExtra("weather_result", false);
                if (ShadowRenderer.this.program != null && bl) {
                    L.v("ok");
                    ShadowRenderer.this.program.updateWeatherDependentParams();
                }
            }
        };
        L.d();
        boolean bl2 = App.get().shadowTestSettings != null && App.get().shadowTestSettings.useAccelTime;
        TimeUtil.setAccelerated(bl2);
        bl2 = App.get().shadowTestSettings != null && App.get().shadowTestSettings.useFastRandomWeather;
        Constants.DEBUG_FAST_RANDOM_WEATHER = bl2;
        this.gravity.set(RESTING_GRAVITY);
        IntentFilter intentFilter = new IntentFilter("weather_result");
        LocalBroadcastManager.getInstance(App.get()).registerReceiver(this.weatherBroadcastReceiver, intentFilter);
        bl2 = App.get().shadowTestSettings != null && App.get().shadowTestSettings.isPulseDisabled ? bl : false;
        this.isOscillationDisabled = bl2;
    }

    @Override
    protected void doDraw() {
    }

    @Override
    protected void doUpdate() {
        boolean bl = this.isOscillationDisabled;
        long l = 0L;
        if (bl) {
            this.shadowPulseTime = 0L;
        } else {
            boolean bl2 = !MathUtil.isBetween(this.unlockTransitionProgress(), 0.0f, 1.0f) && !this.isDragging() && System.currentTimeMillis() - this.upTime > 1000L;
            if (bl2) {
                l = System.currentTimeMillis() - this.lastShadowPulseTimeCheck;
            }
            this.shadowPulseTime += l;
            this.lastShadowPulseTimeCheck = System.currentTimeMillis();
        }
    }

    public PointF downPoint() {
        return this.downPoint;
    }

    public PointF dragPoint() {
        return this.dragPoint;
    }

    public float flingFactor() {
        return this.flingFactor;
    }

    @Override
    public int frameInterval() {
        int n;
        block1: {
            ShadowProgram shadowProgram = this.program;
            n = 1;
            if (shadowProgram == null) {
                return 1;
            }
            float f = this.unlockTransitionProgress();
            int n2 = 3;
            if (f > 0.0f && this.unlockTransitionProgress() < 1.0f) break block1;
            n = !this.isResting ? n2 : (this.isDragging() ? n2 : (this.program.checkAndClearUpdateFlag() ? n2 : 20));
        }
        return n;
    }

    public boolean isDragging() {
        boolean bl = this.downPoint != null;
        return bl;
    }

    public boolean isResting() {
        return this.isResting;
    }

    @Override
    public WallpaperColors onComputeWallpaperColors() {
        ShadowProgram shadowProgram = this.program;
        if (shadowProgram == null) {
            return null;
        }
        return shadowProgram.getWallpaperColors();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(App.get()).unregisterReceiver(this.weatherBroadcastReceiver);
        this.program.destroy();
    }

    @Override
    protected void onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
        float f = ViewConfiguration.get((Context)App.get()).getScaledMaximumFlingVelocity();
        paramFloat1 = (new PointF(paramFloat1, paramFloat2)).length() / f;
        if (paramFloat1 < 0.05F) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ignoring weak fling ");
            stringBuilder.append(paramFloat1);
            L.v(stringBuilder.toString());
        } else if (System.currentTimeMillis() - this.downTime <= 333L) {
            MathUtil.getAngle(paramMotionEvent2.getX() - (downPoint()).x, paramMotionEvent2.getY() - (downPoint()).y);
            paramFloat2 = MathUtil.clamp(MathUtil.getLength(paramMotionEvent2.getX() - (downPoint()).x, paramMotionEvent2.getY() - (downPoint()).y) / viewportShortSide() * 1.25F);
            this.flingFactor = Math.max(paramFloat1, paramFloat2);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("fling ratio ");
            stringBuilder.append(paramFloat1);
            stringBuilder.append(" distance factor ");
            stringBuilder.append(paramFloat2);
            stringBuilder.append(" = ");
            stringBuilder.append(this.flingFactor);
            L.v(stringBuilder.toString());
            this.flingFactor = MathUtil.normalize(this.flingFactor, 0.0F, 0.66F, true);
            stringBuilder = new StringBuilder();
            stringBuilder.append("adjusted fling factor ");
            stringBuilder.append(this.flingFactor);
            L.v(stringBuilder.toString());
            this.flingFlag = true;
            schedulerRequestNow();
        }
    }


    @Override
    protected void onGravitySensor(float f, float f2, float f3) {
        super.onGravitySensor(f, f2, f3);
        f = Math.max(Math.max(Math.abs(this.gravity.x - this.lastGravity.x), Math.abs(this.gravity.y - this.lastGravity.y)), Math.abs(this.gravity.z - this.lastGravity.z));
        this.gravityAccumulator += f * 1000.0f;
        this.gravityAccumulator *= 0.85f;
        if (this.isResting && this.gravityAccumulator > 20.0f) {
            L.v("NOT RESTING");
            this.isResting = false;
            this.schedulerRequestNow();
        } else if (!this.isResting && this.gravityAccumulator < 10.0f) {
            L.v("RESTING");
            this.isResting = true;
            this.gravityAccumulator = 0.0f;
        }
    }

    @Override
    protected void onNotVisible() {
        super.onNotVisible();
        ShadowWallpaperService.get().weatherMan().stop();
    }

    @Override
    public void onOffsetChanged(float f) {
        if (this.program != null && !this.isPreview) {
            super.onOffsetChanged(f);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gL10, int n, int n2) {
        super.onSurfaceChanged(gL10, n, n2);
        GLES20.glViewport(0, 0, this.viewportWidth, this.viewportHeight);
        this.program.onViewportSizeChanged();
    }

    @Override
    public void onSurfaceCreated(GL10 gL10, EGLConfig eGLConfig) {
        super.onSurfaceCreated(gL10, eGLConfig);
        this.program = new ShadowProgram(this);
        this.engine.notifyColorsChanged();
        this.addChildNode(this.program);
    }

    @Override
    protected void onTouchEvent(MotionEvent paramMotionEvent) {
        StringBuilder stringBuilder;
        int i = paramMotionEvent.getAction();
        if (i != 0) {
            if (i != 1)
                if (i != 2) {
                    if (i != 3)
                        return;
                } else {
                    PointF pointF = this.dragPoint;
                    if (pointF != null) {
                        pointF.set(paramMotionEvent.getX(), paramMotionEvent.getY());
                    } else {
                        this.dragPoint = new PointF(paramMotionEvent.getX(), paramMotionEvent.getY());
                    }
                    return;
                }
            this.upTime = System.currentTimeMillis();
            this.downPoint = null;
            if (this.flingFlag) {
                this.flingFlag = false;
            } else {
                this.flingFactor = 0.0F;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("*** up; flingFactor = ");
            stringBuilder.append(this.flingFactor);
            L.v(stringBuilder.toString());
        } else {
            this.downTime = System.currentTimeMillis();
            this.downPoint = new PointF(paramMotionEvent.getX(), paramMotionEvent.getY());
            this.dragPoint = new PointF(paramMotionEvent.getX(), paramMotionEvent.getY());
            this.flingFactor = 0.0F;
            this.flingFlag = false;
            schedulerRequestNow();
        }
    }


    @Override
    protected void onUserPresent() {
        ShadowProgram shadowProgram = this.program;
        if (shadowProgram != null) {
            shadowProgram.onLockScreenUnlocked();
        }
        super.onUserPresent();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        ShadowWallpaperService.get().weatherMan().start();
        ShadowWallpaperService.get().sunriseUtil().get();
    }

    @Override
    protected void onVisibleAtLockScreen() {
        super.onVisibleAtLockScreen();
        ShadowProgram shadowProgram = this.program;
        if (shadowProgram != null) {
            shadowProgram.onVisibleAtLockScreen();
        }
        ShadowWallpaperService.get().weatherMan().start();
        ShadowWallpaperService.get().sunriseUtil().get();
        this.schedulerRequestNow();
    }

    public long shadowPulseTime() {
        return this.shadowPulseTime;
    }

    @Override
    public int unlockTransitionDuration() {
        return 2000;
    }

    public long upTime() {
        return this.upTime;
    }
}

