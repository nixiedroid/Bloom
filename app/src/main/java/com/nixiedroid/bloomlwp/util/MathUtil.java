package com.nixiedroid.bloomlwp.util;

public class MathUtil {
    public static float clamp(float value) {
        return MathUtil.clamp(value, 0.0f, 1.0f);
    }

    public static float clamp(float value, float min, float max) {
        float low = min;
        float high = max;
        if (min > max) {
            high = min;
            low = max;
        }
        if (value < low) {
            return low;
        }
        return Math.min(value, high);
    }

    public static float lerp(float start, float end, float coef) {
        return end + (coef - end) * start;
    }

    public static float map(float f, float f2, float f3, float f4, float f5, boolean bl) {
        f = f2 = MathUtil.lerp(MathUtil.normalize(f, f2, f3), f4, f5);
        if (bl) {
            f = MathUtil.clamp(f2, f4, f5);
        }
        return f;
    }

    public static float normalize(float f, float f2, float f3) {
        return (f - f2) / (f3 - f2);
    }

    public static float normalize(float f, float f2, float f3, boolean bl) {
        f = f2 = (f - f2) / (f3 - f2);
        if (bl) {
            f = MathUtil.clamp(f2, 0.0f, 1.0f);
        }
        return f;
    }

}

