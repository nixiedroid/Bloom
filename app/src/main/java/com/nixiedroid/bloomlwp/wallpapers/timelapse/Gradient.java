package com.nixiedroid.bloomlwp.wallpapers.timelapse;

import com.nixiedroid.bloomlwp.util.ColUtil;

public class Gradient {
    public float[] lower1;
    public float[] lower2;
    public float[] middle;
    public float[] upper1;
    public float[] upper2;

    public Gradient() {
        this.upper1 = new float[3];
        this.upper2 = new float[3];
        this.middle = new float[3];
        this.lower1 = new float[3];
        this.lower2 = new float[3];
    }

    public Gradient(String upper1, String upper2, String middle, String lower1, String lower2) {
        this.upper1 = ColUtil.stringToRgb(upper1);
        this.upper2 = ColUtil.stringToRgb(upper2);
        this.middle = ColUtil.stringToRgb(middle);
        this.lower1 = ColUtil.stringToRgb(lower1);
        this.lower2 = ColUtil.stringToRgb(lower2);
    }

    public static void copyFromTo(final Gradient gradient, final Gradient gradient2) {
        final float[] upper1 = gradient2.upper1;
        final float[] upper2 = gradient.upper1;
        upper1[0] = upper2[0];
        upper1[1] = upper2[1];
        upper1[2] = upper2[2];
        final float[] upper3 = gradient2.upper2;
        final float[] upper4 = gradient.upper2;
        upper3[0] = upper4[0];
        upper3[1] = upper4[1];
        upper3[2] = upper4[2];
        final float[] middle = gradient2.middle;
        final float[] middle2 = gradient.middle;
        middle[0] = middle2[0];
        middle[1] = middle2[1];
        middle[2] = middle2[2];
        final float[] lower1 = gradient2.lower1;
        final float[] lower2 = gradient.lower1;
        lower1[0] = lower2[0];
        lower1[1] = lower2[1];
        lower1[2] = lower2[2];
        final float[] lower3 = gradient2.lower2;
        final float[] lower4 = gradient.lower2;
        lower3[0] = lower4[0];
        lower3[1] = lower4[1];
        lower3[2] = lower4[2];
    }

    public static void lerp(Gradient gradient, Gradient gradient2, float f, Gradient gradient3) {
        ColUtil.lerpRgb(f, gradient.upper1, gradient2.upper1, gradient3.upper1);
        ColUtil.lerpRgb(f, gradient.upper2, gradient2.upper2, gradient3.upper2);
        ColUtil.lerpRgb(f, gradient.middle, gradient2.middle, gradient3.middle);
        ColUtil.lerpRgb(f, gradient.lower1, gradient2.lower1, gradient3.lower1);
        ColUtil.lerpRgb(f, gradient.lower2, gradient2.lower2, gradient3.lower2);
    }
}

