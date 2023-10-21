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
import com.nixiedroid.bloomlwp.util.Vec3f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class UtRenderer extends RenderNode implements GLSurfaceView.Renderer {
    private final RenderScheduler scheduler;
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

    @SuppressWarnings("deprecation")
    public UtRenderer() {
        L.d();
        scheduler = new RenderScheduler(this);
        DisplayManager displayManager = (DisplayManager) App.get().getSystemService(Context.DISPLAY_SERVICE);
        Point point = new Point();
        displayManager.getDisplay(0).getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;
    }

    public int displayShortSide() {
        return Math.min(displayWidth, displayHeight);
    }

    public abstract int frameInterval();

    public GLSurfaceView glSurfaceView() {
        return engine.glSurfaceView();
    }

    public boolean isLockScreen() {
        return isLockScreen;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public abstract WallpaperColors onComputeWallpaperColors();

    public void onDestroy() {
        L.d();
        scheduler.kill();
    }

    @Override
    public void onDrawFrame(GL10 gL10) {
        //Maybe remove nanotime?
        scheduler.onDrawFrameStart();
        //System.nanoTime();
        update();
        //System.nanoTime();
        draw();
        //System.nanoTime();
        scheduler.onDrawFrameEnd();
    }

    protected void onFling() {
    }

    protected void onGravitySensor(float f, float f2, float f3) {
        if (lastGravity == null) {
            lastGravity = new Vec3f(f, f2, f3);
        } else {
            lastGravity.set(gravity);
        }
        gravity.set(f, f2, f3);
    }

    protected void onNotVisible() {
        L.d();
        isVisible = false;
        scheduler.setVisible(false);
        scheduler.cancel();
    }

    public void onOffsetChanged(float f) {
        homeOffset = f;
    }

    protected void onScreenOff() {
        L.d();
        scheduler.cancel();
    }

    protected void onScreenOn() {
        L.d("and is visible? " + isVisible);
        if (isVisible) {
            schedulerRequestNow();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 object, int displayWidth, int displayHeight) {
        L.d(displayWidth + "x" + displayHeight);
        viewportWidth = displayWidth;
        viewportHeight = displayHeight;
        viewportShortSide = Math.min(viewportWidth, viewportHeight);
        multiplier1440 = (float) viewportShortSide / 1440.0f;
        surfaceChangeTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceCreated(GL10 gL10, EGLConfig eGLConfig) {
        L.d();
        GLES20.glEnable(36784);
        GLES20.glHint(36784, 36785);
    }

    public void onSurfaceRedrawNeeded() {
        if (isFirstSurfaceRedraw) {
            isFirstSurfaceRedraw = false;
            L.d();
        }
    }

    protected void onTouchEvent() {
    }

    protected void onUserPresent() {
        L.d();
        if (isLockScreen()) {
            L.d("and was at lock screen");
            isLockScreen = false;
            scheduler.requestRenderNow();
        }
    }

    protected void onVisible() {
        L.d();
        isVisible = true;
        scheduler.setVisible(true);
        scheduler.requestRenderNow();
    }

    protected void onVisibleAtLockScreen() {
        L.d();
        isVisible = true;
        isLockScreen = true;
        scheduler.setVisible(true);
        scheduler.requestRenderNow();
    }

    public void schedulerRequestNow() {
        scheduler.requestRenderNow();
    }

    public void setEngine(UtWallpaperService.UtEngine utEngine) {
        engine = utEngine;
    }

    public void setIsPreview(boolean bl) {
        isPreview = bl;
    }
    public int viewportHeight() {
        return viewportHeight;
    }

    public int viewportWidth() {
        return viewportWidth;
    }
}

