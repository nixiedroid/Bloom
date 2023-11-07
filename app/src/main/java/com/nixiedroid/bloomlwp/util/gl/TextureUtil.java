package com.nixiedroid.bloomlwp.util.gl;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.nixiedroid.bloomlwp.util.ColUtil;
import com.nixiedroid.bloomlwp.util.L;

public class TextureUtil {
    private static final int white = 0xFF_FF_FF_FF;
    private static final int transparent = 0x00_FF_FF_FF;
    private static Bitmap getBottomTexture(int width, int height){
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{transparent,white,white,white});
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,width,height);
        drawable.draw(canvas);
        return bitmap;
    }
    private static Bitmap getTopTexture(int width, int height){
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{white,white,transparent});
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,width,height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static int loadBottomTexture(int width, int height){
        Bitmap texture = getBottomTexture(width,height);
        return loadTexture(texture);
    }
    public static int loadTopTexture(int width, int height){
        Bitmap texture = getTopTexture(width,height);
        return loadTexture(texture);
    }

    private static int loadTexture(Bitmap bmp) {
        int[] responseArray = new int[1];
        GLES20.glGenTextures(1, responseArray, 0);
        if (responseArray[0] == 0) {
            L.e("Couldn't generate texture object");
            return 0;
        }
        L.v(bmp.getWidth() + "x" + bmp.getHeight());
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, responseArray[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return responseArray[0];
    }

    public static int loadTexture(Context context, int textureResId) {
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
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, responseArray[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, decodeResource, 0);
        decodeResource.recycle();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return responseArray[0];
    }

}

