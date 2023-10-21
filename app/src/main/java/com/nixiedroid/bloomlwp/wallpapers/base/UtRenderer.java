package com.nixiedroid.bloomlwp.wallpapers.base;

import android.app.WallpaperColors;
import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.util.Vec3f;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class UtRenderer
        extends RenderNode
        implements GLSurfaceView.Renderer {
    protected int displayHeight;
    protected int displayWidth;
    protected UtWallpaperService.UtEngine engine;
    // protected long fpsStartTime = System.nanoTime();
    protected Vec3f gravity = new Vec3f(0.0f, 0.0f, 1.0f);
    protected float homeOffset;
    protected boolean isLockScreen;
    protected boolean isPreview;
    protected boolean isVisible;
    protected Vec3f lastGravity;
    protected float multiplier1440;
    protected long surfaceChangeTime;
    protected int viewportHeight;
    protected int viewportShortSide;
    protected int viewportWidth;
    private boolean isFirstSurfaceRedraw = true;
    private final RenderScheduler scheduler;
    @SuppressWarnings("deprecation")
    public UtRenderer() {
        L.d();
        this.scheduler = new RenderScheduler(this);
        DisplayManager displayManager = (DisplayManager) App.get().getSystemService(Context.DISPLAY_SERVICE);
        Point point = new Point();
        displayManager.getDisplay(0).getSize(point);
        this.displayWidth = point.x;
        this.displayHeight = point.y;
    }

    public int displayShortSide() {
        return Math.min(this.displayWidth, this.displayHeight);
    }

    public abstract int frameInterval();

    public GLSurfaceView glSurfaceView() {
        return this.engine.glSurfaceView();
    }

    public Vec3f gravity() {
        return this.gravity;
    }

    public boolean isLockScreen() {
        return this.isLockScreen;
    }

    public boolean isPortrait() {
        return this.viewportHeight > this.viewportWidth;
    }

    public boolean isPreview() {
        return this.isPreview;
    }

    public float multiplier1440() {
        return this.multiplier1440;
    }

    public abstract WallpaperColors onComputeWallpaperColors();

    public void onDestroy() {
        L.d();
        this.scheduler.kill();
    }

    @Override
    public void onDrawFrame(GL10 gL10) {
        //Maybe remove nanotime?
        this.scheduler.onDrawFrameStart();
        System.nanoTime();
        this.update();
        System.nanoTime();
        this.draw();
        System.nanoTime();
        this.scheduler.onDrawFrameEnd();
    }

    protected void onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
    }

    protected void onGravitySensor(float f, float f2, float f3) {
        Vec3f vec3f = this.lastGravity;
        if (vec3f == null) {
            this.lastGravity = new Vec3f(f, f2, f3);
        } else {
            vec3f.set(this.gravity);
        }
        this.gravity.set(f, f2, f3);
    }

    protected void onNotVisible() {
        L.d();
        this.isVisible = false;
        this.scheduler.setVisible(false);
        this.scheduler.cancel();
    }

    public void onOffsetChanged(float f) {
        this.homeOffset = f;
    }

    protected void onScreenOff() {
        L.d();
        this.scheduler.cancel();
    }

    protected void onScreenOn() {
        L.d("and is visible? " +
                this.isVisible);
        if (this.isVisible) {
            this.schedulerRequestNow();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 object, int n, int n2) {
        L.d(n + "x" + n2);
        this.viewportWidth = n;
        this.viewportHeight = n2;
        this.viewportShortSide = Math.min(this.viewportWidth, this.viewportHeight);
        this.multiplier1440 = (float) this.viewportShortSide / 1440.0f;
        this.surfaceChangeTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceCreated(GL10 gL10, EGLConfig eGLConfig) {
        L.d();
        GLES20.glEnable(36784);
        GLES20.glHint(36784, 36785);
    }

    public void onSurfaceRedrawNeeded() {
        if (!this.isFirstSurfaceRedraw) {
            return;
        }
        this.isFirstSurfaceRedraw = false;
        L.d();
    }

    protected void onTouchEvent(MotionEvent motionEvent) {
    }

    protected void onUserPresent() {
        L.d();
        if (this.isLockScreen()) {
            L.d("and was at lock screen");
            this.isLockScreen = false;
            this.scheduler.requestRenderNow();
        }
    }

    protected void onVisible() {
        L.d();
        this.isVisible = true;
        this.scheduler.setVisible(true);
        this.scheduler.requestRenderNow();
    }

    protected void onVisibleAtLockScreen() {
        L.d();
        this.isVisible = true;
        this.isLockScreen = true;
        this.scheduler.setVisible(true);
        this.scheduler.requestRenderNow();
    }

    public void schedulerRequestNow() {
        this.scheduler.requestRenderNow();
    }

    public void setEngine(UtWallpaperService.UtEngine utEngine) {
        this.engine = utEngine;
    }

    public void setIsPreview(boolean bl) {
        this.isPreview = bl;
    }

    public int unlockTransitionDuration() {
        return 1000;
    }

    public float unlockTransitionProgress() {
        if (this.isLockScreen()) {
            return 0.0f;
        }
        if (this.isPreview) {
            return 1.0f;
        }
        return MathUtil.clamp((float) TimeUtil.elapsedRealTimeSince(TimeUtil.unlockTime()) / (float) this.unlockTransitionDuration());
    }

    public int viewportHeight() {
        return this.viewportHeight;
    }

    public int viewportShortSide() {
        return this.viewportShortSide;
    }

    public int viewportWidth() {
        return this.viewportWidth;
    }
}

