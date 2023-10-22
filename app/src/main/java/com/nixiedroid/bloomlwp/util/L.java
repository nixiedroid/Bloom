package com.nixiedroid.bloomlwp.util;

import android.util.Log;

public class L {
    public static boolean decorationEnabled = true;
    public static int levelFilter;

    public static void d() {
        if (levelFilter <= 1) {
            L.output("", 1, Thread.currentThread().getStackTrace());
        }
    }

    public static void d(String message) {
        if (levelFilter <= 1) {
            L.output(message, 1, Thread.currentThread().getStackTrace());
        }
    }

    public static void e(String message) {
        if (levelFilter <= 5) {
            L.output(message, 4, Thread.currentThread().getStackTrace());
        }
    }

    public static void v(String message) {
        if (levelFilter <= 0) {
            L.output(message, 0, Thread.currentThread().getStackTrace());
        }
    }

    public static void w(String message) {
        if (levelFilter <= 3) {
            L.output(message, 3, Thread.currentThread().getStackTrace());
        }
    }
    public static void e(String message, boolean doPrintStackTrace) {
        if (levelFilter <= 5) {
            L.output(message, 4, Thread.currentThread().getStackTrace());
            if (doPrintStackTrace) {
                L.printStackTrace();
            }
        }
    }

    private static void output(String message, int level, StackTraceElement[] stackTrace) {
        String[] split;
        if (decorationEnabled) {
            split = stackTrace[3].getClassName().split("\\.");
            message = split[split.length - 1] + "() " + message;
        }
        if (level == 0) {
            Log.v("zz", message);
        } else if (level == 1) {
            Log.d("zz", message);
        } else if (level == 2) {
            Log.i("zz", message);
        } else if (level == 3) {
            Log.w("zz", message);
        } else if (level != 4) {
            Log.v("zz", message);
        } else {
            Log.e("zz", message);
        }
    }


    public static void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder str = new StringBuilder();
        for (int i = 4; i < stackTrace.length; i++) {
            str = new StringBuilder(
                    str + "    " +
                    stackTrace[i].getClassName() + "." +
                    stackTrace[i].getMethodName() + " @ " +
                    stackTrace[i].getLineNumber() + "\n"
            );
        }
        Log.e("zz", str.toString());

    }


}

