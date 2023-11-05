package com.nixiedroid.bloomlwp.wallpaper.bloom;

import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.wallpaper.base.DoubleEasyQuad;
import com.nixiedroid.bloomlwp.wallpaper.base.Layer;

public class LowerGradientLayer
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
    private int viewportWidth;
    private float y;

    public LowerGradientLayer(BloomRenderer renderNode, BloomProgram object, int n, boolean bl) {
        this.renderer = renderNode;
        this.program = object;
        this.textureId = n;
        this.isTransitionLayer = bl;
        this.quad = new DoubleEasyQuad(false, 0.18f);
        quad.setProgramIds(this.program.aPositionLocation, this.program.aColorLocation, this.program.aTextureCoordinatesLocation);
    }

    private void updateAlphas() {
        if (this.isTransitionLayer) {
            this.alpha = this.renderer.isLockScreen() ? 1.0f : this.renderer.unlockBottomFadeoutAnimValue();
        } else {
            this.alpha = this.renderer.isLockScreen() ? 0.0f : this.renderer.unlockBottomFadeInAnimValue();
        }
    }

    private void updateGradientColors() {
        this.quad.setColorsVerticalGradient(this.renderer.gradient().lower1, this.renderer.gradient().lower2);
    }

    private void updatePositions() {
        float f = this.y;
        if (this.isTransitionLayer){
            this.y = this.defaultY;
        } else {
            if (this.renderer.isLockScreen()){
                this.y = this.unlockStartY;
            } else {
                this.y = MathUtil.lerp(this.renderer.unlockBottomSlideAnimValue(), this.unlockStartY, this.defaultY);
            }
        }
        if (this.y != f) {
            this.quad.setBasePositions(0.0f, this.y, this.viewportWidth, this.height + this.y);
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

    public void setDimensions(int width, int height) {
        this.viewportWidth = width;
        this.defaultY = (float) height * 0.296875f;
        this.height = (float) height * 0.703125f;
        this.unlockStartY = (float) height * 0.66f;
        this.y = this.defaultY - 1.0f;
    }
}

