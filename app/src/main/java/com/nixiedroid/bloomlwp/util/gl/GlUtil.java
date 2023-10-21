package com.nixiedroid.bloomlwp.util.gl;

import android.app.ActivityManager;
import android.content.Context;

public class GlUtil {
    public static boolean supportsEs2(Context context) {
        return ((ActivityManager)context
                .getSystemService(Context.ACTIVITY_SERVICE))
                .getDeviceConfigurationInfo().reqGlEsVersion >= glVersionToValue(2,0);
    }
    @SuppressWarnings("SameParameterValue")
    private static int glVersionToValue(int major, int minor){
        return (major << 16) + minor;
    }
}

