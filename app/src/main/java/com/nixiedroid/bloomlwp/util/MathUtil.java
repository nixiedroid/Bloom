package com.nixiedroid.bloomlwp.util;

import android.graphics.PointF;

public class MathUtil {
    public static float clamp(float f) {
        return MathUtil.clamp(f, 0.0f, 1.0f);
    }

    public static float clamp(float f, float boundaryOne, float boundaryTwo) {
        float low = boundaryOne;
        float high = boundaryTwo;
        if (boundaryOne > boundaryTwo) {
            high = boundaryOne;
            low = boundaryTwo;
        }
        if (f < low) {
            return low;
        }
        return Math.min(f, high);
    }

    public static float getAngle(float f, float f2) {
        return (float)Math.atan2(f2, f);
    }

    public static float getLength(float f, float f2) {
        return (float)Math.sqrt(f * f + f2 * f2);
    }

    public static boolean isBetween(float f, float f2, float f3) {
        return f > f2 && f < f3;
    }

    public static float lerp(float f, float f2, float f3) {
        return f2 + (f3 - f2) * f;
    }

    public static float map(float f, float f2, float f3, float f4, float f5) {
        return MathUtil.map(f, f2, f3, f4, f5, false);
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

    public static void pointOnCircle(float f, float f2, float f3, float f4, PointF pointF) {
        pointF.x = (float)(Math.cos(f4) * (double) f3) + f;
        pointF.y = (float)((double) f3 * Math.sin(f4)) + f2;
    }

    public static void pointOnCircle(float f, float f2, PointF pointF) {
        pointF.x = (float)(Math.cos(f2) * (double) f);
        pointF.y = (float)((double) f * Math.sin(f2));
    }

    public static void rotate(PointF pointF, float f, PointF pointF2) {
        double d = f;
        f = (float)(Math.cos(d) * (double)pointF.x - Math.sin(d) * (double)pointF.y);
        float f2 = (float)(Math.sin(d) * (double)pointF.x + Math.cos(d) * (double)pointF.y);
        pointF2.x = f;
        pointF2.y = f2;
    }
}

