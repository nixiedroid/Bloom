package com.nixiedroid.bloomlwp.wallpapers.orbit;

import android.app.WallpaperColors;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.wallpapers.base.UtRenderer;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OrbitRenderer
        extends UtRenderer {
    private final float[] matrix;
    private final float dayPercentOffset;
    private PointF downPoint;
    private long downTime;
    private PointF dragPoint;
    private long isFlingingUntil;
    private float offsetRatio;
    private float offsetVelocity;
    private Phase phase;
    private OrbitPrograms programs;
    private final Time time;

    public OrbitRenderer() {
        this.phase = OrbitRenderer.Phase.RESTING;
        this.matrix = new float[16];
        final OrbitTestSettings orbitTestSettings = App.get().orbitTestSettings;
        TimeUtil.setAccelerated(orbitTestSettings != null && App.get().orbitTestSettings.useAccelTime);
        this.time = new Time();
        float dayPercent;
        if (
                App.get().orbitTestSettings != null &&
                        App.get().orbitTestSettings.presetTimeCode != null
        ) {
            dayPercent = TimeUtil.dayPercentFromHoursMinutes(12, 19.9f);
        } else {
            dayPercent = TimeUtil.nowDayPercent();
        }
        this.dayPercentOffset = TimeUtil.nowDayPercent() - dayPercent;
    }

    private void updateOffset() {
        switch (phase.ordinal()) {
            default: {
                break;
            }
            case 8: {
                float f = this.offsetVelocity;
                float f2 = this.offsetRatio;
                this.offsetVelocity = f + 0.01f * f2;
                this.offsetVelocity *= 0.85f;
                this.offsetRatio = f2 - this.offsetVelocity;
                break;
            }
            case 7: {
                float f = this.offsetVelocity;
                float f3 = this.offsetRatio;
                this.offsetVelocity = f + 0.014f * f3;
                this.offsetVelocity *= 0.85f;
                this.offsetRatio = f3 - this.offsetVelocity;
                break;
            }
            case 5:
            case 6: {
                if (this.dragPoint == null) {
                    return;
                }
                float f = 133;
                float f4 = f / 1440.0f * (float) this.viewportShortSide();
                f = f * 1.5f / 1440.0f;
                PointF pointF = this.dragPoint;
                float f5 = pointF.x;
                PointF pointF2 = this.downPoint;
                f = Math.min(MathUtil.getLength((f5 - pointF2.x) * f, (pointF.y - pointF2.y) * f), f4) / f4;
                f4 = this.offsetRatio;
                this.offsetRatio = f4 + (f - f4) * 0.08f;
                break;
            }
            case 4: {
                float f = this.offsetVelocity;
                float f6 = this.offsetRatio;
                this.offsetVelocity = f + 0.008f * f6;
                this.offsetVelocity *= 0.85f;
                this.offsetRatio = f6 - this.offsetVelocity;
                break;
            }
            case 3: {
                this.offsetRatio = 1.0f;
                break;
            }
            case 2: {
                float f = this.offsetVelocity;
                float f7 = this.offsetRatio;
                this.offsetVelocity = f + (f7 - 1.0f) * 0.008f;
                this.offsetVelocity *= 0.85f;
                this.offsetRatio = f7 - this.offsetVelocity;
            }
            case 1:
        }
    }

    private void updatePhase() {
        Phase phase;
        boolean bl;
        if (this.phase == Phase.FLINGING && System.currentTimeMillis() > this.isFlingingUntil) {
            this.phase = Phase.POST_FLING;
        }
        bl = Math.abs(this.offsetRatio) < 0.001f;
        if (Math.abs(this.offsetRatio) < 0.001f && Math.abs(this.offsetVelocity) < 0.001f) {
            phase = this.phase;
            if (phase == Phase.LOCK_ANIMIN) {
                this.phase = Phase.LOCKED_RESTING;
                this.offsetRatio = 1.0f;
                this.offsetVelocity = 0.0f;
            } else if (phase == Phase.POST_LOCK || phase == Phase.POST_DRAG || phase == Phase.POST_FLING) {
                this.phase = Phase.RESTING;
                this.offsetRatio = 0.0f;
                this.offsetVelocity = 0.0f;
            }
        }
        if (bl && (phase = this.phase) != Phase.RESTING && phase != Phase.DRAGGING && phase != Phase.LOCKED_RESTING) {
            this.phase = Phase.RESTING;
            this.offsetRatio = 0.0f;
            this.offsetVelocity = 0.0f;
        }
    }

    @Override
    protected void doDraw() {
    }

    @Override
    protected void doUpdate() {
        float f = TimeUtil.nowDayPercent();
        this.time.setDayPercent(f - this.dayPercentOffset);
        this.updatePhase();
        this.updateOffset();
    }

    @Override
    public int frameInterval() {
        Phase phase = this.phase;
        int n = phase != Phase.RESTING && phase != Phase.LOCKED_RESTING ? 0 : 1;
        n = n != 0 ? 3 : 2;
        return n;
    }

    public float[] matrix() {
        return this.matrix;
    }

    public float offsetRatio() {
        return this.offsetRatio;
    }

    @Override
    public WallpaperColors onComputeWallpaperColors() {
        OrbitPrograms orbitPrograms = this.programs;
        if (orbitPrograms == null) {
            return null;
        }
        return orbitPrograms.getWallpaperColors();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onFling(MotionEvent object, MotionEvent motionEvent, float f, float f2) {
        Phase phase = this.phase;
        if (phase != Phase.LOCK_ANIMIN && phase != Phase.LOCKED_RESTING && phase != Phase.POST_LOCK) {
            float f3 = ViewConfiguration.get(App.get()).getScaledMaximumFlingVelocity();
            if (!((f = new PointF(f, f2).length() / f3) < 0.05f) && System.currentTimeMillis() - this.downTime <= 333L) {
                int n = (int) (MathUtil.map(Math.max(f, MathUtil.clamp(MathUtil.getLength(motionEvent.getX() - this.downPoint.x, motionEvent.getY() - this.downPoint.y) / (float) this.viewportShortSide() * 1.15f)), 0.0f, 1.0f, 0.33f, 1.0f, true) * 75.0f);
                this.isFlingingUntil = System.currentTimeMillis() + (long) n;
                this.phase = Phase.FLINGING;
            }
        }
    }

    @Override
    protected void onScreenOn() {
        super.onScreenOn();
        if (!this.isVisible) {
            this.phase = Phase.RESTING;
            this.offsetRatio = 0.0f;
            this.offsetVelocity = 0.0f;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gL10, int n, int n2) {
        super.onSurfaceChanged(gL10, n, n2);
        GLES20.glViewport(0, 0, this.viewportWidth, this.viewportHeight);
        Matrix.orthoM(this.matrix, 0, 0.0f, this.viewportWidth, this.viewportHeight, 0.0f, 0.0f, 1.0f);
        this.programs.onViewportSizeChanged();
    }

    @Override
    public void onSurfaceCreated(GL10 gL10, EGLConfig eGLConfig) {
        super.onSurfaceCreated(gL10, eGLConfig);
        this.removeChildNodes();
        this.programs = new OrbitPrograms(this);
        this.addChildNode(this.programs);
        this.engine.notifyColorsChanged();
    }

    @Override
    protected void onTouchEvent(MotionEvent motionEvent) {
        if (this.phase != Phase.LOCK_ANIMIN && this.phase != Phase.LOCKED_RESTING && this.phase != Phase.POST_LOCK) {
            switch (motionEvent.getAction()) {
                case 0:
                    this.phase = Phase.DRAGGING;
                    this.downTime = System.currentTimeMillis();
                    this.downPoint = new PointF(motionEvent.getX(), motionEvent.getY());
                    this.dragPoint = new PointF(motionEvent.getX(), motionEvent.getY());
                    this.offsetRatio = 0.0f;
                    this.isFlingingUntil = 0L;
                    return;
                case 1:

                case 3:
                    if (this.phase != Phase.FLINGING) {
                        this.phase = Phase.POST_DRAG;
                        return;
                    }
                    return;
                case 2:
                    if (dragPoint == null) {
                        this.dragPoint = new PointF(motionEvent.getX(), motionEvent.getY());
                    } else {
                        dragPoint.set(motionEvent.getX(), motionEvent.getY());
                    }
                    break;
                default:

            }
        }
    }

    @Override
    protected void onUserPresent() {
        super.onUserPresent();
        this.phase = Phase.POST_LOCK;
        if (!(this.offsetRatio > 1.0f)) {
            this.offsetRatio = 1.0f;
        }
    }

    @Override
    protected void onVisibleAtLockScreen() {
        super.onVisibleAtLockScreen();
        this.phase = Phase.LOCK_ANIMIN;
        this.offsetRatio = 2.0f;
    }

    public Phase phase() {
        return this.phase;
    }

    public Time time() {
        return this.time;
    }

    public enum Phase {
        RESTING,
        DRAGGING,
        POST_DRAG,
        FLINGING,
        POST_FLING,
        LOCK_ANIMIN,
        LOCKED_RESTING,
        POST_LOCK

    }
}

