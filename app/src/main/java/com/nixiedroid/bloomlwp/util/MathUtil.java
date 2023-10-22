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

    public static float lerp(float start, float end, float coeff) {
        return end + (coeff - end) * start;
    }

    public static float map(float start, float delta, float end, float end2, float coeff, boolean doClamp) {
        float lerp = lerp(normalize(start, delta, end), end2, coeff);
        return doClamp ? clamp(lerp, end2, coeff) : lerp;
    }

    public static float normalize(float start, float delta, float end) {
        return (start - delta) / (end - delta);
    }

    public static float normalize(float start, float delta, float end, boolean doClamp) {
        float f4 = normalize(start, delta, end);
        return doClamp ? clamp(f4, 0.0f, 1.0f) : f4;
    }

}

