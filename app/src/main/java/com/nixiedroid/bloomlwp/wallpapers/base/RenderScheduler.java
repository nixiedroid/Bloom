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

    private void scheduleNext(int millis) {
        if (millis <= 0) {
            this.runnable.run();
            return;
        }
        this.handler.removeCallbacks(this.runnable);
        this.handler.postDelayed(this.runnable, millis);
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
//        this.scheduleNext(
//                (int) ((float) this.renderer.frameInterval() * 16.666666f - (float) (SystemClock.elapsedRealtimeNanos() - this.startNs) / 1000000.0f)
//        );
        this.scheduleNext((this.renderer.frameInterval() << 4) - (int)  (SystemClock.elapsedRealtimeNanos() - this.startNs) / 1000000);
    }

    public void onDrawFrameStart() {
        this.startNs = SystemClock.elapsedRealtimeNanos();
    }

    public void requestRenderNow() {
        renderer.glSurfaceView().requestRender();
        scheduleNext(this.renderer.frameInterval() << 4);
        //scheduleNext((int) ((float) this.renderer.frameInterval() * 16.666666f));
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }
}

