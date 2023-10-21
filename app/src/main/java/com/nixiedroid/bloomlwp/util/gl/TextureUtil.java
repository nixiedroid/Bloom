package com.nixiedroid.bloomlwp.util.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.nixiedroid.bloomlwp.util.L;

public class TextureUtil {
    public static int loadTexture(Context context, int i, boolean z) {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        if (iArr[0] == 0) {
            L.e("Couldn't generate texture object");
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i, options);
        L.v(decodeResource.getWidth() + "x" + decodeResource.getHeight());
        if (decodeResource == null) {
            L.e("Resource ID " + i + " could not be decoded.");
            GLES20.glDeleteTextures(1, iArr, 0);
            return 0;
        }
        GLES20.glBindTexture(3553, iArr[0]);
        if (z) {
            GLES20.glTexParameteri(3553, 10241, 9987);
            GLES20.glTexParameteri(3553, 10240, 9729);
        } else {
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
        }
        GLUtils.texImage2D(3553, 0, decodeResource, 0);
        decodeResource.recycle();
        if (z) {
            GLES20.glGenerateMipmap(3553);
        }
        GLES20.glBindTexture(3553, 0);
        return iArr[0];
    }

}

