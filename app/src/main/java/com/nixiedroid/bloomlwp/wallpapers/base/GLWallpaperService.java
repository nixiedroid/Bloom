/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.base;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public abstract class GLWallpaperService extends WallpaperService {
    protected abstract int defaultGlSurfaceViewRenderMode();

    public class GLEngine
    extends WallpaperService.Engine {
        protected WallpaperGLSurfaceView glSurfaceView;
        protected boolean isRendererSet;

        public GLEngine() {
            super();
        }

        public GLSurfaceView glSurfaceView() {
            return glSurfaceView;
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
            glSurfaceView.setEGLContextFactory(new LowPrioContextFactory());
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onDestroy();
            glSurfaceView = null;
        }
        @SuppressWarnings("SameParameterValue")
        protected void setEGLContextClientVersion(int versoion) {
            glSurfaceView.setEGLContextClientVersion(versoion);
        }

        @SuppressWarnings("SameParameterValue")
        protected void setPreserveEGLContextOnPause(boolean preserveEGLContextOnPause) {
            glSurfaceView.setPreserveEGLContextOnPause(preserveEGLContextOnPause);
        }

        protected void setRenderer(GLSurfaceView.Renderer renderer) {
            glSurfaceView.setRenderer(renderer);
            glSurfaceView.setRenderMode(defaultGlSurfaceViewRenderMode());
            isRendererSet = true;
        }

        class LowPrioContextFactory implements GLSurfaceView.EGLContextFactory {
            LowPrioContextFactory() {
            }

            @Override
            public EGLContext createContext(EGL10 eGL10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
                return eGL10.eglCreateContext(
                        eGLDisplay,
                        eGLConfig,
                        EGL10.EGL_NO_CONTEXT,
                        new int[]{12440, 2, 12544, 12547, 12344});
            }

            @Override
            public void destroyContext(EGL10 eGL10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
                eGL10.eglDestroyContext(eGLDisplay, eGLContext);
            }
        }

        class WallpaperGLSurfaceView
        extends GLSurfaceView {
            WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return GLEngine.this.getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }
}
