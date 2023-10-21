package com.nixiedroid.bloomlwp.wallpapers.base;

import android.os.Handler;
import android.os.SystemClock;

public class RenderScheduler {
    private final Handler handler;
    private final Runnable runnable;
    private UtRenderer renderer;
    private long startNs;
    private boolean visible = true;
    @SuppressWarnings("deprecation")
    public RenderScheduler(UtRenderer utRenderer) {
        runnable = () -> RenderScheduler.this.renderer.glSurfaceView().requestRender();
        renderer = utRenderer;
        handler = new Handler();
    }

    private void scheduleNext(int n) {
        if (n <= 0) {
            this.runnable.run();
        } else {
            this.handler.removeCallbacks(this.runnable);
            this.handler.postDelayed(this.runnable, n);
        }
    }

    public void cancel() {
        this.handler.removeCallbacks(this.runnable);
    }

    public void kill() {
        this.cancel();
        this.renderer = null;
    }

    public void onDrawFrameEnd() {
        if (!this.visible) {
            return;
        }
        this.scheduleNext((int) ((float) this.renderer.frameInterval() * 16.666666f - (float) (SystemClock.elapsedRealtimeNanos() - this.startNs) / 1000000.0f));
    }

    public void onDrawFrameStart() {
        this.startNs = SystemClock.elapsedRealtimeNanos();
    }

    public void requestRenderNow() {
        this.renderer.glSurfaceView().requestRender();
        this.scheduleNext((int) ((float) this.renderer.frameInterval() * 16.666666f));
    }

    public void setVisible(boolean bl) {
        this.visible = bl;
    }
}

