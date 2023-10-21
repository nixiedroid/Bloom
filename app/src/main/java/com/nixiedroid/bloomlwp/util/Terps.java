package com.nixiedroid.bloomlwp.util;

import android.view.animation.*;

@SuppressWarnings({"SpellCheckingInspection"})
public class Terps {
    private static final AccelerateInterpolator accelerate05;
    private static final AccelerateInterpolator accelerate15;
    private static final EaseOutElasticInterpolator easeOutElastic;
    private static final AccelerateDecelerateInterpolator inOut;
    private static final LinearInterpolator linear;

    static {
        linear = new LinearInterpolator();
        accelerate05 = new AccelerateInterpolator(0.5f);
        accelerate15 = new AccelerateInterpolator(1.5f);
        inOut = new AccelerateDecelerateInterpolator();
        easeOutElastic = new EaseOutElasticInterpolator();
    }

    public static AccelerateInterpolator accelerate05() {
        return accelerate05;
    }

    public static AccelerateDecelerateInterpolator inOut() {
        return inOut;
    }

    public static Interpolator linear() {
        return linear;
    }

    public static class EaseOutElasticInterpolator
    extends BaseInterpolator {
        @Override
        public float getInterpolation(float f) {
            return (float)(Math.pow(2.0, -10.0f * f) * Math.sin((double)(f - 0.075f) * (Math.PI * 2) / (double)0.3f) + 1.0);
        }
    }
}

