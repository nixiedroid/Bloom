package com.nixiedroid.bloomlwp.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BaseInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class Terps {
    private static final AccelerateInterpolator accelerate05;
    private static final AccelerateInterpolator accelerate1;
    private static final AccelerateInterpolator accelerate15;
    private static final AccelerateInterpolator accelerate2;
    private static final DecelerateInterpolator decelerate1;
    private static final DecelerateInterpolator decelerate15;
    private static final DecelerateInterpolator decelerate2;
    private static final DecelerateInterpolator decelerate3;
    private static final EaseOutElasticInterpolator easeOutElastic;
    private static final AccelerateDecelerateInterpolator inOut;
    private static final LinearInterpolator linear;

    static {
        linear = new LinearInterpolator();
        decelerate1 = new DecelerateInterpolator(1.0f);
        decelerate15 = new DecelerateInterpolator(1.5f);
        decelerate2 = new DecelerateInterpolator(2.0f);
        decelerate3 = new DecelerateInterpolator(3.0f);
        accelerate1 = new AccelerateInterpolator(1.0f);
        accelerate05 = new AccelerateInterpolator(0.5f);
        accelerate15 = new AccelerateInterpolator(1.5f);
        accelerate2 = new AccelerateInterpolator(2.0f);
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

