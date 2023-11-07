package com.nixiedroid.bloomlwp.wallpaper.base;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.util.Vec3f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class Renderer extends RenderNode implements GLSurfaceView.Renderer {
    public static final int GL_CPU_OPTIMIZED_QCOM = 36785;
    private static final int GL_BINNING_CONTROL_HINT_QCOM = 36784;
    private final RenderScheduler scheduler;
    protected int displayHeight;
    protected int displayWidth;
    protected WallpaperService.Engine engine;
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
    public Renderer() {
        L.d();
        scheduler = new RenderScheduler(this);
        DisplayManager dm = (DisplayManager) App.get().getSystemService(Context.DISPLAY_SERVICE);
        WindowManager wm = (WindowManager) App.get().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
            WindowInsets windowInsets = windowMetrics.getWindowInsets();
            Insets insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());

            int insetsWidth = insets.right + insets.left;
            int insetsHeight = insets.top + insets.bottom;

            Rect b = windowMetrics.getBounds();
            displayWidth = b.width() - insetsWidth;
            displayHeight = b.height() - insetsHeight;
        } else {
            Point point = new Point();
            dm.getDisplay(0).getSize(point);
            displayWidth = point.x;
            displayHeight = point.y;
        }
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

    public abstract Object onComputeWallpaperColors();

    public void onDestroy() {
        L.d();
        scheduler.kill();
    }

    @Override
    public void onDrawFrame(GL10 gL10) {
        scheduler.onDrawFrameStart();
        System.nanoTime();
        update();
        System.nanoTime();
        draw();
        System.nanoTime();
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

        GLES20.glEnable(GL_BINNING_CONTROL_HINT_QCOM);
        GLES20.glHint(GL_BINNING_CONTROL_HINT_QCOM, GL_CPU_OPTIMIZED_QCOM);
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
        L.d("On visible");
        isVisible = true;
        scheduler.setVisible(true);
        scheduler.requestRenderNow();
    }

    protected void onVisibleAtLockScreen() {
        L.d("On visible lockscreen");
        isVisible = true;
        isLockScreen = true;
        scheduler.setVisible(true);
        scheduler.requestRenderNow();
    }

    public void schedulerRequestNow() {
        scheduler.requestRenderNow();
    }

    public void setEngine(WallpaperService.Engine utEngine) {
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

    public int getDisplayHeight() {
        return displayHeight;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }
}

