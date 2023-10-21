package com.nixiedroid.bloomlwp.util;

import android.view.animation.Interpolator;

public class AnimFloat {
    private final long delayNs;
    private boolean doneFlag;
    private final long durationNs;
    private final float endValue;
    private boolean isRunning = false;
    private long startNs;
    private final float startValue;
    @SuppressWarnings("SpellCheckingInspection")
    private final Interpolator terp;

    public AnimFloat(float startValue, float endValue, long durationS, long delayS, Interpolator interpolator) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.durationNs = durationS * 1000_000;
        this.delayNs = delayS * 1000_000;
        this.terp = interpolator;
    }

    public AnimFloat(float startValue, float endValue, long durationS, Interpolator interpolator) {
        this(startValue, endValue, durationS, 0L, interpolator);
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public float percent() {
        if (this.doneFlag) {
            return 1.0f;
        }
        if (!this.isRunning) {
            return 0.0f;
        }
        float animDistance = MathUtil.clamp((float)(System.nanoTime() - this.startNs - this.delayNs) / (float)this.durationNs);
        if (animDistance == 1.0f && !this.doneFlag) {
            this.isRunning = false;
            this.doneFlag = true;
        }
        return animDistance;
    }

    public void setToEnd() {
        this.isRunning = false;
        this.doneFlag = true;
        this.startNs = 0;
    }

    public void start() {
        this.isRunning = true;
        this.startNs = System.nanoTime();
        this.doneFlag = false;
    }

    public float value() {
        float animDistance = MathUtil.clamp(this.percent());
        return MathUtil.lerp(this.terp.getInterpolation(animDistance), this.startValue, this.endValue);
    }
}

