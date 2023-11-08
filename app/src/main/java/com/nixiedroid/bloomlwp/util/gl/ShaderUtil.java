package com.nixiedroid.bloomlwp.util.gl;

import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.util.L;

public class ShaderUtil {
    public static int buildProgram(String vertexCode, String fragmentCode) {
        int programId = linkProgram(compileVertexShader(vertexCode), compileFragmentShader(fragmentCode));
        return validateProgram(programId);
    }


    public static int compileFragmentShader(String shader) {
        return ShaderUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, shader);
    }
    public static int compileVertexShader(String shader) {
        return ShaderUtil.compileShader(GLES20.GL_VERTEX_SHADER, shader);
    }

    private static int compileShader(int shaderType, String shader) {
        int glCreateShader = GLES20.glCreateShader(shaderType);
        if (glCreateShader == 0) {
            L.e("Could not create new shader");
            return 0;
        }
        GLES20.glShaderSource(glCreateShader, shader);
        GLES20.glCompileShader(glCreateShader);
        int[] responseArray = new int[1];
        GLES20.glGetShaderiv(glCreateShader, GLES20.GL_COMPILE_STATUS, responseArray, 0);
        if (responseArray[0] != 0) {
            return glCreateShader;
        }
        String glGetShaderInfoLog = GLES20.glGetShaderInfoLog(glCreateShader);
        L.e("shader failed to compile: " + glGetShaderInfoLog, true);
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }



    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram == 0) {
            L.e("Could not create new program");
            return 0;
        }
        GLES20.glAttachShader(glCreateProgram, vertexShaderId);
        GLES20.glAttachShader(glCreateProgram, fragmentShaderId);
        GLES20.glLinkProgram(glCreateProgram);
        int[] responseArray = new int[1];
        GLES20.glGetProgramiv(glCreateProgram, GLES20.GL_LINK_STATUS, responseArray, 0);
        if (responseArray[0] != 0) {
            return glCreateProgram;
        }
        L.e("program failed: " + GLES20.glGetProgramInfoLog(glCreateProgram));
        GLES20.glDeleteProgram(glCreateProgram);
        return 0;
    }


    public static int makeProgram(int vertexShaderResId, int fragmentShaderResId) {
        return ShaderUtil.buildProgram(
                TextResourceReader.readTextFileFromResource(vertexShaderResId),
                TextResourceReader.readTextFileFromResource(fragmentShaderResId)
        );
    }

    public static int validateProgram(int programId) {
        GLES20.glValidateProgram(programId);
        int[] responseArray = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_VALIDATE_STATUS, responseArray, 0);
        if (responseArray[0] == 0) {
            L.e("did not validate: " + responseArray[0] + " - " + GLES20.glGetProgramInfoLog(programId));
            return 0;
        }
        L.v("is valid");
        return programId;
    }
}

