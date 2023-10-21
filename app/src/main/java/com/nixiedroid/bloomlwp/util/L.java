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

    public static void d(String string2) {
        if (levelFilter <= 1) {
            L.output(string2, 1, Thread.currentThread().getStackTrace());
        }
    }

    public static void e(String string2) {
        if (levelFilter <= 5) {
            L.output(string2, 4, Thread.currentThread().getStackTrace());
        }
    }

    public static void e(String string2, boolean bl) {
        if (levelFilter <= 5) {
            L.output(string2, 4, Thread.currentThread().getStackTrace());
            if (bl) {
                L.printStackTrace();
            }
        }
    }

    private static void output(String str, int i, StackTraceElement[] stackTraceElementArr) {
        String str2 = stackTraceElementArr[3].getClassName();
        String methodName = stackTraceElementArr[3].getMethodName();
        if (decorationEnabled) {
            str = str2 + "." + methodName + "() " + str;
        }
        if (i == 0) {
            Log.v("zz", str);
        } else if (i == 1) {
            Log.d("zz", str);
        } else if (i == 2) {
            Log.i("zz", str);
        } else if (i == 3) {
            Log.w("zz", str);
        } else if (i != 4) {
            Log.v("zz", str);
        } else {
            Log.e("zz", str);
        }
    }


    public static void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder str = new StringBuilder();
        for (int i = 4; i < stackTrace.length; i++) {
            str = new StringBuilder((
                    str
                            + "    "
                            + stackTrace[i].getClassName()
                            + "." + stackTrace[i].getMethodName()
                            + " @ " + stackTrace[i].getLineNumber())
                    + "\n");
        }
        Log.e("zz", str.toString());

    }

    public static void v(String string2) {
        if (levelFilter <= 0) {
            L.output(string2, 0, Thread.currentThread().getStackTrace());
        }
    }

    public static void w(String string2) {
        if (levelFilter <= 3) {
            L.output(string2, 3, Thread.currentThread().getStackTrace());
        }
    }
}

