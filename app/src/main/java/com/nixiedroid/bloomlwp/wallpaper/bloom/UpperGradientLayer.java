package com.nixiedroid.bloomlwp.wallpaper.bloom;

import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.wallpaper.base.DoubleEasyQuad;
import com.nixiedroid.bloomlwp.wallpaper.base.Layer;

public class UpperGradientLayer
extends Layer {
    private float alpha;
    private float defaultY;
    private float height;
    private final boolean isTransitionLayer;
    private final BloomProgram program;
    private final DoubleEasyQuad quad;
    private final BloomRenderer renderer;
    private final int textureId;
    private float unlockStartY;
    private int viewportHeight;
    private int viewportWidth;
    private float y;

    public UpperGradientLayer(BloomRenderer renderer, BloomProgram program, int n, boolean bl) {
        this.renderer = renderer;
        this.program = program;
        this.textureId = n;
        this.isTransitionLayer = bl;
        final DoubleEasyQuad quad = new DoubleEasyQuad(true, 0.66f);
        this.quad = quad;
        quad.setProgramIds(program.aPositionLocation, program.aColorLocation, program.aTextureCoordinatesLocation);
    }

    private void updateAlphas() {
        if (this.isTransitionLayer) {
            float f = this.renderer.isLockScreen() ? 1.0f : this.renderer.unlockTopFadeoutAnimValue();
            this.alpha = f;
        } else {
            float f = this.renderer.isLockScreen() ? 0.0f : this.renderer.unlockTopFadeInAnimValue();
            this.alpha = f;
        }
    }

    private void updateGradientColors() {
        this.quad.setColorsVerticalGradient(this.renderer.gradient().upper1, this.renderer.gradient().upper2);
    }

    private void updatePositions() {
        float f = this.y;
        this.y = this.isTransitionLayer ? this.defaultY : (this.renderer.isLockScreen() ? this.unlockStartY : MathUtil.lerp(this.renderer.unlockTopSlideAnimValue(), this.unlockStartY, this.defaultY));
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
        this.viewportHeight = n2;
        this.defaultY = 0.0f;
        this.height = (float)this.viewportHeight * 0.44140625f;
        this.unlockStartY = this.height * 0.8f * -1.0f;
        this.y = this.defaultY - 1.0f;
    }
}

