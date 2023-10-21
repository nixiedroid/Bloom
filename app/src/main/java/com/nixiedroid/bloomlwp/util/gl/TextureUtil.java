package com.nixiedroid.bloomlwp.util.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.nixiedroid.bloomlwp.util.L;

public class TextureUtil {
    public static int loadTexture(Context context, int textureResId, boolean isMipmap) {
        int[] responseArray = new int[1];
        GLES20.glGenTextures(1, responseArray, 0);
        if (responseArray[0] == 0) {
            L.e("Couldn't generate texture object");
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), textureResId, options);
        L.v(decodeResource.getWidth() + "x" + decodeResource.getHeight());
        if (decodeResource == null) {
            L.e("Resource ID " + textureResId + " could not be decoded.");
            GLES20.glDeleteTextures(1, responseArray, 0);
            return 0;
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, responseArray[0]);
        if (isMipmap) {
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        } else {
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, decodeResource, 0);
        decodeResource.recycle();
        if (isMipmap) {
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return responseArray[0];
    }

}

