package com.nixiedroid.bloomlwp.util;

import android.view.animation.*;

@SuppressWarnings({"SpellCheckingInspection"})
public class Terps {
    private static final AccelerateInterpolator accelerate05;
    private static final AccelerateDecelerateInterpolator inOut;
    private static final LinearInterpolator linear;

    static {
        linear = new LinearInterpolator();
        accelerate05 = new AccelerateInterpolator(0.5f);
        inOut = new AccelerateDecelerateInterpolator();
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
}

