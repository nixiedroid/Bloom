/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.timelapse;

import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.wallpapers.base.DoubleEasyQuad;
import com.nixiedroid.bloomlwp.wallpapers.base.Layer;

public class LowerGradientLayer
extends Layer {
    private float alpha;
    private float defaultY;
    private float height;
    private final boolean isTransitionLayer;
    private final TimelapseProgram program;
    private DoubleEasyQuad quad;
    private final TimelapseRenderer renderer;
    private final int textureId;
    private float unlockStartY;
    private int viewportHeight;
    private int viewportWidth;
    private float y;

    public LowerGradientLayer(TimelapseRenderer renderNode, TimelapseProgram object, int n, boolean bl) {
        this.renderer = renderNode;
        this.program = object;
        this.textureId = n;
        this.isTransitionLayer = bl;
        this.quad = new DoubleEasyQuad(false, 0.18f);
        quad.setProgramIds(this.program.aPositionLocation, this.program.aColorLocation, this.program.aTextureCoordinatesLocation);
    }

    private void updateAlphas() {
        if (this.isTransitionLayer) {
            float f = this.renderer.isLockScreen() ? 1.0f : this.renderer.unlockBottomFadeoutAnimValue();
            this.alpha = f;
        } else {
            float f = this.renderer.isLockScreen() ? 0.0f : this.renderer.unlockBottomFadeInAnimValue();
            this.alpha = f;
        }
    }

    private void updateGradientColors() {
        this.quad.setColorsVerticalGradient(this.renderer.gradient().lower1, this.renderer.gradient().lower2);
    }

    private void updatePositions() {
        float f = this.y;
        this.y = this.isTransitionLayer ? this.defaultY : (this.renderer.isLockScreen() ? this.unlockStartY : MathUtil.lerp(this.renderer.unlockBottomSlideAnimValue(), this.unlockStartY, this.defaultY));
        float f2 = this.y;
        if (f2 != f) {
            this.quad.setBasePositions(0.0f, f2, this.viewportWidth, this.height + f2);
        }
    }

    @Override
    protected void doDraw() {
        if (this.alpha == 0.0f) {
            return;
        }
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.textureId);
        GLES20.glUniform1f(this.program.uAlpha, this.alpha);
        this.quad.draw();
    }

    @Override
    protected void doUpdate() {
        this.updateAlphas();
        if (this.alpha > 0.0f) {
            this.updatePositions();
            this.updateGradientColors();
        }
    }

    public void setDimensions(int n, int n2) {
        this.viewportWidth = n;
        n = this.viewportHeight = n2;
        this.defaultY = (float)n * 0.296875f;
        this.height = (float)n * 0.703125f;
        this.unlockStartY = (float)n * 0.66f;
        this.y = this.defaultY - 1.0f;
    }
}

