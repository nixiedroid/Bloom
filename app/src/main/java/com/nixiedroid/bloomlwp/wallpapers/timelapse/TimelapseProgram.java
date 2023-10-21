package com.nixiedroid.bloomlwp.wallpapers.timelapse;

import android.opengl.GLES20;
import android.opengl.Matrix;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.gl.ShaderProgram;
import com.nixiedroid.bloomlwp.util.gl.TextureUtil;

public class TimelapseProgram
extends ShaderProgram {
    public final int aColorLocation;
    public final int aPositionLocation;
    public final int aTextureCoordinatesLocation;
    private final LowerGradientLayer lowerLayer;
    private final float[] matrix = new float[16];
    private final TimelapseRenderer renderer;
    private long startTime;
    private final LowerGradientLayer transitionLowerLayer;
    private final UpperGradientLayer transitionUpperLayer;
    public final int uAlpha;
    public final int uMatrixLocation;
    public final int uTextureUnitLocation;
    public final int uTime;
    private final UpperGradientLayer upperLayer;

    public TimelapseProgram(TimelapseRenderer timelapseRenderer) {
        super(App.get(), R.raw.timelapse_vertex_shader, R.raw.timelapse_fragment_shader);
        this.renderer = timelapseRenderer;
        this.startTime = System.nanoTime();
        this.uMatrixLocation = GLES20.glGetUniformLocation(this.programId, "uMatrix");
        this.uTextureUnitLocation = GLES20.glGetUniformLocation(this.programId, "uTextureUnit");
        this.uAlpha = GLES20.glGetUniformLocation(this.programId, "uAlpha");
        this.uTime = GLES20.glGetUniformLocation(this.programId, "uTime");
        this.aPositionLocation = GLES20.glGetAttribLocation(this.programId, "aPosition");
        this.aTextureCoordinatesLocation = GLES20.glGetAttribLocation(this.programId, "aTextureCoordinates");
        this.aColorLocation = GLES20.glGetAttribLocation(this.programId, "aColor");
        int n = this.renderer.displayShortSide() > 1080 ? R.drawable.timelapse_top_gradient_1440 : R.drawable.timelapse_top_gradient_1080;
        int upperTextureId = TextureUtil.loadTexture(App.get(), n, false);
        n = this.renderer.displayShortSide() > 1080 ? R.drawable.timelapse_bottom_gradient_1440 : R.drawable.timelapse_bottom_gradient_1080;
        int lowerTextureId = TextureUtil.loadTexture(App.get(), n, false);
        this.transitionUpperLayer = new UpperGradientLayer(this.renderer, this, upperTextureId, true);
        this.upperLayer = new UpperGradientLayer(this.renderer, this, upperTextureId, false);
        this.transitionLowerLayer = new LowerGradientLayer(this.renderer, this, lowerTextureId, true);
        this.lowerLayer = new LowerGradientLayer(this.renderer, this, lowerTextureId, false);
        this.addChildNode(this.upperLayer);
        this.addChildNode(this.transitionUpperLayer);
        this.addChildNode(this.lowerLayer);
        this.addChildNode(this.transitionLowerLayer);
    }

    @Override
    protected void doDraw() {
        GLES20.glUseProgram(this.programId);
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(1, 771);
        GLES20.glActiveTexture(33984);
        GLES20.glUniform1i(this.uTextureUnitLocation, 0);
        GLES20.glUniformMatrix4fv(this.uMatrixLocation, 1, false, this.matrix, 0);
        float f = (float)(System.nanoTime() - this.startTime) / 1.0E-9f;
        GLES20.glUniform1f(this.uTime, f);
    }

    @Override
    protected void doUpdate() {
    }

    public void onViewportSizeChanged() {
        Matrix.orthoM(this.matrix, 0, 0.0f, this.renderer.viewportWidth(), this.renderer.viewportHeight(), 0.0f, 0.0f, 1.0f);
        this.upperLayer.setDimensions(this.renderer.viewportWidth(), this.renderer.viewportHeight());
        this.transitionUpperLayer.setDimensions(this.renderer.viewportWidth(), this.renderer.viewportHeight());
        this.lowerLayer.setDimensions(this.renderer.viewportWidth(), this.renderer.viewportHeight());
        this.transitionLowerLayer.setDimensions(this.renderer.viewportWidth(), this.renderer.viewportHeight());
    }

    public void onVisible() {
        this.startTime = System.nanoTime();
    }

    public void onVisibleAtLockScreen() {
        this.onVisible();
    }
}

