/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.util.gl;

import android.content.Context;
import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.util.L;

public class ShaderUtil {
    public static int buildProgram(String vertexCode, String fragmentCode) {
        int programId = linkProgram(compileVertexShader(vertexCode), compileFragmentShader(fragmentCode));
        validateProgram(programId);
        return programId;
    }


    public static int compileFragmentShader(String string2) {
        return ShaderUtil.compileShader(35632, string2);
    }

    private static int compileShader(int n, String shader) {
        int glCreateShader = GLES20.glCreateShader(n);
        if (glCreateShader == 0) {
            L.e("Could not create new shader");
            return 0;
        }
        GLES20.glShaderSource(glCreateShader, shader);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        String glGetShaderInfoLog = GLES20.glGetShaderInfoLog(glCreateShader);
        L.e("shader failed to compile: " + glGetShaderInfoLog, true);
        GLES20.glDeleteShader(glCreateShader);
        return 0;

    }

    public static int compileVertexShader(String string2) {
        return ShaderUtil.compileShader(35633, string2);
    }

    public static int linkProgram(int i, int i2) {
        int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram == 0) {
            L.e("Could not create new program");
            return 0;
        }
        GLES20.glAttachShader(glCreateProgram, i);
        GLES20.glAttachShader(glCreateProgram, i2);
        GLES20.glLinkProgram(glCreateProgram);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateProgram;
        }
        L.e("program failed: " + GLES20.glGetProgramInfoLog(glCreateProgram));
        GLES20.glDeleteProgram(glCreateProgram);
        return 0;
    }



    public static int makeProgram(Context context, int n, int n2) {
        return ShaderUtil.buildProgram(TextResourceReader.readTextFileFromResource(context, n), TextResourceReader.readTextFileFromResource(context, n2));
    }

    public static boolean validateProgram(int n) {
        GLES20.glValidateProgram(n);
        boolean bl = true;
        int[] nArray = new int[1];
        GLES20.glGetProgramiv(n, 35715, nArray, 0);
        if (nArray[0] == 0) {
            bl = false;
        }
        if (!bl) {
            L.e("did not validate: " + nArray[0] + " - " + GLES20.glGetProgramInfoLog(n));
        } else {
            L.v("is valid");
        }

        return bl;
    }
}

