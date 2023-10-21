/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.shadow;

import android.animation.ArgbEvaluator;
import android.app.WallpaperColors;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.Toast;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.*;
import com.nixiedroid.bloomlwp.util.gl.ShaderProgram;
import com.nixiedroid.bloomlwp.wallpapers.base.EasyQuad;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;
import com.nixiedroid.bloomlwp.wallpapers.util.WeatherVo;

public class ShadowProgram
        extends ShaderProgram {
    private static final float[] BASE_RADII_1440 = new float[]{1280.0f, 640.0f, 320.0f, 160.0f};
    public final int heightUniformId;
    public final int highlightCenterUniformId;
    public final int highlightColorUniformId;
    public final int highlightInnerRadiusUniformId;
    public final int highlightOuterRadiusUniformId;
    public final int mainCenterUniformId;
    public final int mainColorUniformId;
    public final int mainRadiusUniformId;
    public final int matrixUniformId;
    public final int outerColorUniformId;
    public final int positionAttributeId;
    public final int shadowCenterUniformId;
    public final int shadowColorUniformId;
    public final int shadowInnerRadiusUniformId;
    public final int shadowOuterRadiusUniformId;
    public final int textureCoordsAttributeId;
    public final int widthUniformId;
    private final EasyQuad quad;
    private float[] baseRadii;
    private float baseShadowAlpha;
    private float[] baseShadowRgb;
    private Config config;
    private PointF defaultCenter;
    private PointF[] dragOffsetTargets;
    private PointF[] dragOffsetVelocities = new PointF[4];
    private PointF[] dragOffsets = new PointF[4];
    private float[] highlightCenter;
    private float[] highlightColor;
    private float[] highlightInnerRadius;
    private float[] highlightOuterRadius;
    private float[] mainCenter;
    private float[] mainColor;
    private float[] mainRadius;
    private float[] matrix;
    private float[] outerColor;
    private ShadowRenderer renderer;
    private float ringRotation;
    private PointF scratch;
    private float shadowAlphaMultiplier;
    private float shadowAlphaMultiplierTarget;
    private float[] shadowCenter;
    private float[] shadowColor;
    private float shadowGradientLength1440;
    private float shadowGradientLength1440Target;
    private float[] shadowInnerRadius;
    private float[] shadowOuterRadius;
    private Vec3f tweeningGravity;
    private AnimFloat[] unlockScaleAnims;
    private boolean updateFastFlag;
    private boolean viewportSizeChangeFlag;

    public ShadowProgram(ShadowRenderer shadowRenderer) {
        super(App.get(), R.raw.shadow_vertex_shader, R.raw.shadow_fragment_shader);
        this.dragOffsetTargets = new PointF[4];
        this.defaultCenter = new PointF();
        this.scratch = new PointF();
        this.matrix = new float[16];
        this.mainCenter = new float[8];
        this.mainRadius = new float[4];
        this.mainColor = new float[16];
        this.outerColor = new float[16];
        this.shadowCenter = new float[8];
        this.shadowOuterRadius = new float[4];
        this.shadowInnerRadius = new float[4];
        this.shadowColor = new float[16];
        this.highlightCenter = new float[8];
        this.highlightOuterRadius = new float[4];
        this.highlightInnerRadius = new float[4];
        this.highlightColor = new float[16];
        this.baseRadii = new float[4];
        this.baseShadowRgb = new float[3];
        this.tweeningGravity = new Vec3f(0.0f, 0.0f, 1.0f);
        this.renderer = shadowRenderer;
        this.config = ShadowWallpaperService.get().config();
        this.positionAttributeId = GLES20.glGetAttribLocation(this.programId, "aPosition");
        this.textureCoordsAttributeId = GLES20.glGetAttribLocation(this.programId, "aTextureCoordinates");
        this.matrixUniformId = GLES20.glGetUniformLocation(this.programId, "uMatrix");
        this.widthUniformId = GLES20.glGetUniformLocation(this.programId, "width");
        this.heightUniformId = GLES20.glGetUniformLocation(this.programId, "height");
        this.mainCenterUniformId = GLES20.glGetUniformLocation(this.programId, "mainCenter");
        this.mainRadiusUniformId = GLES20.glGetUniformLocation(this.programId, "mainRadius");
        this.mainColorUniformId = GLES20.glGetUniformLocation(this.programId, "mainColor");
        this.shadowCenterUniformId = GLES20.glGetUniformLocation(this.programId, "shadowCenter");
        this.shadowOuterRadiusUniformId = GLES20.glGetUniformLocation(this.programId, "shadowOuterRadius");
        this.shadowInnerRadiusUniformId = GLES20.glGetUniformLocation(this.programId, "shadowInnerRadius");
        this.shadowColorUniformId = GLES20.glGetUniformLocation(this.programId, "shadowColor");
        this.highlightCenterUniformId = GLES20.glGetUniformLocation(this.programId, "highlightCenter");
        this.highlightOuterRadiusUniformId = GLES20.glGetUniformLocation(this.programId, "highlightOuterRadius");
        this.highlightInnerRadiusUniformId = GLES20.glGetUniformLocation(this.programId, "highlightInnerRadius");
        this.highlightColorUniformId = GLES20.glGetUniformLocation(this.programId, "highlightColor");
        this.outerColorUniformId = GLES20.glGetUniformLocation(this.programId, "outerColor");
        this.quad = new EasyQuad();
        this.quad.setProgramIds(this.positionAttributeId, -1, this.textureCoordsAttributeId);
        this.initColors();
        this.tweeningGravity.set(ShadowRenderer.RESTING_GRAVITY);
        this.updateWeatherDependentParams();
        this.shadowAlphaMultiplier = this.shadowAlphaMultiplierTarget;
        this.shadowGradientLength1440 = this.shadowGradientLength1440Target;
        this.initUnlockAnims();
        for (int i = 0; i < 4; ++i) {
            this.dragOffsetVelocities[i] = new PointF();
            this.dragOffsets[i] = new PointF();
            this.dragOffsetTargets[i] = new PointF();
        }
    }

    private float calcOscillationAlphaFactor() {
        long l = this.renderer.shadowPulseTime() % 16000L;
        float f = l < 2000L ? 1.0f : (l < 8000L ? MathUtil.map(l, 2000.0f, 8000.0f, 1.0f, 0.2f, true) : (l < 10000L ? 0.2f : MathUtil.map(l, 10000.0f, 16000.0f, 0.2f, 1.0f, true)));
        return f;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private PointF calcPositionOffset(int var1) {
        boolean var7 = false;
        int var8 = Math.max(var1, 0);
        float var3 = (float) this.renderer.viewportShortSide() * 0.008F;
        this.scratch.x = MathUtil.map(this.tweeningGravity.x, -0.75F, 0.75F, 1.5707964F, -1.5707964F, true);
        PointF var9 = this.scratch;
        var9.x = (float) Math.sin((double) var9.x);
        var9 = this.scratch;
        var9.x *= var3;
        float var2 = var9.x;
        float var4 = (float) (3 - var8);
        var9.x = var2 * var4;
        var9.y = MathUtil.map(this.tweeningGravity.y, 0.0F, 1.0F, -1.5707964F, 0.7853982F, true);
        var9 = this.scratch;
        var9.y = (float) Math.sin((double) var9.y);
        var9 = this.scratch;
        var9.y *= var3;
        var9.y *= var4;
        this.renderer.isDragging();
        if (this.renderer.isDragging()) {
            var4 = 0.72F;
        } else {
            var4 = 0.8F;
        }

        if (this.renderer.flingFactor() != 0.0F) {
            var2 = this.renderer.flingFactor();
            if ((float) (System.currentTimeMillis() - this.renderer.upTime()) < var2 * 350.0F) {
                var7 = true;
            }
        }

        PointF[] var15;
        if (this.renderer.isDragging()) {
            if (this.renderer.downPoint() != null && this.renderer.dragPoint() != null) {
                label55:
                {
                    var2 = (float) (4 - var1) * 0.03F;

                    float var5;
                    float var6;
                    label44:
                    {
                        label56:
                        {
                            try {
                                var3 = this.renderer.dragPoint().x;
                                var5 = this.renderer.downPoint().x;
                            } catch (Exception var14) {
                                var3 = 0.0F;
                                break label56;
                            }

                            var3 = (var3 - var5) * var2;

                            try {
                                var6 = this.renderer.dragPoint().y;
                                var5 = this.renderer.downPoint().y;
                                break label44;
                            } catch (Exception var13) {
                            }
                        }

                        var2 = 0.0F;
                        break label55;
                    }

                    var2 *= var6 - var5;
                }
            } else {
                var2 = 0.0F;
                var3 = 0.0F;
            }

            MathUtil.pointOnCircle(MathUtil.clamp(MathUtil.getLength(var3, var2), 0.0F, this.renderer.multiplier1440() * 27.5F * ((float) (3 - var1) / 3.0F)), MathUtil.getAngle(var3, var2), this.dragOffsetTargets[var1]);
        } else if (!var7) {
            var15 = this.dragOffsetTargets;
            var15[var1].x = 0.0F;
            var15[var1].y = 0.0F;
        }

        PointF[] var10 = this.dragOffsetVelocities;
        PointF var12 = var10[var1];
        var3 = var12.x;
        PointF[] var11 = this.dragOffsetTargets;
        var2 = var11[var1].x;
        var15 = this.dragOffsets;
        var12.x = var3 + (var2 - var15[var1].x) * 0.072F;
        var12 = var10[var1];
        var12.y += (var11[var1].y - var15[var1].y) * 0.072F;
        PointF var17 = var10[var1];
        var17.x *= var4;
        var17 = var10[var1];
        var17.y *= var4;
        var17 = var15[var1];
        var17.x += var10[var1].x;
        var17 = var15[var1];
        var17.y += var10[var1].y;
        PointF var16 = this.scratch;
        return new PointF(var16.x + var15[var1].x, var16.y + var15[var1].y);

    }

    private void initColors() {
        int[] var6 = this.config.mainColors;
        int var3 = var6.length;
        int var2 = 0;

        int var1;
        float[] var4;
        float[] var5;
        for (var1 = 0; var2 < var3; ++var2) {
            var5 = ColUtil.intToRgb(var6[var2]);
            var4 = this.mainColor;
            var4[var1] = var5[0];
            var4[var1 + 1] = var5[1];
            var4[var1 + 2] = var5[2];
            var4[var1 + 3] = 1.0F;
            var1 += 4;
        }

        var4 = ColUtil.intToRgb(this.config.surfaceColor);
        var5 = this.outerColor;
        var5[0] = var4[0];
        var5[1] = var4[1];
        var5[2] = var4[2];
        var5[3] = 1.0F;

        for (var1 = 1; var1 < 4; ++var1) {
            var2 = var1 * 4;
            var3 = (var1 - 1) * 4;
            var4 = this.outerColor;
            var5 = this.mainColor;
            var4[var2] = var5[var3];
            var4[var2 + 1] = var5[var3 + 1];
            var4[var2 + 2] = var5[var3 + 2];
            var4[var2 + 3] = 1.0F;
        }

        for (var1 = 0; var1 < 4; ++var1) {
            Config var7 = this.config;
            if (var1 == 0) {
                var2 = var7.surfaceColor;
            } else {
                var2 = var7.mainColors[var1 - 1];
            }

            var3 = ColUtil.rgbToInt(ShadowColors.highlightRgb());
            var4 = ColUtil.intToRgb((Integer) (new ArgbEvaluator()).evaluate(this.config.highlightAlpha, var2, var3));
            var5 = this.highlightColor;
            var2 = var1 * 4;
            var5[var2] = var4[0];
            var5[var2 + 1] = var4[1];
            var5[var2 + 2] = var4[2];
            var5[var2 + 3] = 1.0F;
        }

    }

    private void initUnlockAnims() {
        Interpolator object = new PathInterpolator(0.55f, 0.0f, 0.45f, 1.0f);
        long l = 1300;
        AnimFloat animFloat0 = new AnimFloat(0.75f, 1.0f, l, 0L, (Interpolator) object);
        AnimFloat animFloat = new AnimFloat(0.75f, 1.0f, l, 290L, (Interpolator) object);
        AnimFloat animFloat2 = new AnimFloat(0.75f, 1.0f, l, 500L, (Interpolator) object);
        AnimFloat animFloat3 = new AnimFloat(0.75f, 1.0f, l, 700L, new PathInterpolator(0.6f, 0.0f, 0.4f, 1.0f));
        AnimFloat[] animFloatArray = this.unlockScaleAnims = new AnimFloat[]{animFloat0, animFloat, animFloat2, animFloat3};
        int n = animFloatArray.length;
        for (AnimFloat aFloat : animFloatArray) {
            aFloat.setToEnd();
        }
    }

    private void setFastUpdateFlag() {
        this.updateFastFlag = true;
    }

    private void updateShadowColorArray() {
        float f = this.baseShadowAlpha;
        float f2 = this.calcOscillationAlphaFactor();
        int n = ColUtil.rgbToInt(this.baseShadowRgb);
        for (int i = 0; i < 4; ++i) {
            int n2 = ColUtil.multiply(this.config.mainColors[i], n);
            n2 = (Integer) new ArgbEvaluator().evaluate(f * f2, (Object) this.config.mainColors[i], (Object) n2);
            float[] fArray = this.shadowColor;
            int n3 = i * 4;
            ColUtil.intToRgb(n2, fArray, n3);
            this.shadowColor[n3 + 3] = 1.0f;
        }
    }

    private void updateTweeningGravity() {
        if (this.renderer.isResting()) {
            if (!Vec3f.equals(this.tweeningGravity, ShadowRenderer.RESTING_GRAVITY)) {
                this.tweeningGravity.lerp(0.13f, ShadowRenderer.RESTING_GRAVITY);
                if (Vec3f.distance(this.tweeningGravity, ShadowRenderer.RESTING_GRAVITY) < 0.02f) {
                    this.tweeningGravity.set(ShadowRenderer.RESTING_GRAVITY);
                }
            }
        } else {
            this.tweeningGravity.lerp(0.33f, this.renderer.gravity());
        }
    }

    public boolean checkAndClearUpdateFlag() {
        boolean bl = this.updateFastFlag;
        if (bl) {
            this.updateFastFlag = false;
        }
        return bl;
    }

    public void destroy() {
        this.renderer = null;
    }

    @Override
    protected void doDraw() {
        GLES20.glUseProgram(this.programId);
        if (this.viewportSizeChangeFlag) {
            GLES20.glUniformMatrix4fv(this.matrixUniformId, 1, false, this.matrix, 0);
            GLES20.glUniform1f(this.widthUniformId, this.renderer.viewportWidth());
            GLES20.glUniform1f(this.heightUniformId, this.renderer.viewportHeight());
            GLES20.glUniform4fv(this.mainColorUniformId, 4, this.mainColor, 0);
            GLES20.glUniform4fv(this.outerColorUniformId, 4, this.outerColor, 0);
            this.viewportSizeChangeFlag = false;
        }
        GLES20.glUniform2fv(this.mainCenterUniformId, 4, this.mainCenter, 0);
        GLES20.glUniform1fv(this.mainRadiusUniformId, 4, this.mainRadius, 0);
        GLES20.glUniform2fv(this.shadowCenterUniformId, 4, this.shadowCenter, 0);
        GLES20.glUniform1fv(this.shadowOuterRadiusUniformId, 4, this.shadowOuterRadius, 0);
        GLES20.glUniform1fv(this.shadowInnerRadiusUniformId, 4, this.shadowInnerRadius, 0);
        GLES20.glUniform4fv(this.shadowColorUniformId, 4, this.shadowColor, 0);
        GLES20.glUniform1fv(this.highlightOuterRadiusUniformId, 4, this.highlightOuterRadius, 0);
        GLES20.glUniform1fv(this.highlightInnerRadiusUniformId, 4, this.highlightInnerRadius, 0);
        GLES20.glUniform2fv(this.highlightCenterUniformId, 4, this.highlightCenter, 0);
        GLES20.glUniform4fv(this.highlightColorUniformId, 4, this.highlightColor, 0);
        this.quad.draw();
    }

    @Override
    protected void doUpdate() {
        this.updateTweeningGravity();
        boolean var8 = false;

        float var1;
        float var2;
        int var5;
        float[] var9;
        float[] var13;
        for (var5 = 0; var5 < 4; ++var5) {
            int var6 = var5 * 2;
            int var7 = var6 + 1;
            var9 = this.mainCenter;
            var1 = var9[var6];
            var2 = var9[var7];
            PointF var10 = this.calcPositionOffset(var5);
            var9 = this.mainCenter;
            PointF var11 = this.defaultCenter;
            var9[var6] = (float) ((int) (var11.x + var10.x));
            var9[var7] = (float) ((int) (var11.y + var10.y));
            if (Math.abs(var9[var6] - var1) >= 1.0F || Math.abs(this.mainCenter[var7] - var2) >= 1.0F) {
                this.setFastUpdateFlag();
            }

            PointF var12 = this.scratch;
            var12.x = 0.0F;
            var12.y = this.renderer.multiplier1440() * 20.0F;
            var12 = this.scratch;
            MathUtil.rotate(var12, this.ringRotation, var12);
            float[] var14 = this.shadowCenter;
            var9 = this.mainCenter;
            var1 = var9[var6];
            var10 = this.scratch;
            var14[var6] = var1 + var10.x;
            var14[var7] = var9[var7] + var10.y;
            var10.x = 0.0F;
            var10.y = this.renderer.multiplier1440() * 10.0F;
            var12 = this.scratch;
            MathUtil.rotate(var12, this.ringRotation, var12);
            var13 = this.highlightCenter;
            var9 = this.mainCenter;
            var1 = var9[var6];
            var11 = this.scratch;
            var13[var6] = var1 + var11.x;
            var13[var7] = var9[var7] + var11.y;
        }

        var1 = this.shadowGradientLength1440;
        var2 = this.shadowGradientLength1440Target;
        if (var1 < var2) {
            this.shadowGradientLength1440 = var1 + 0.2F;
            if (Math.abs(var2 - this.shadowGradientLength1440) < 0.2F) {
                this.shadowGradientLength1440 = this.shadowGradientLength1440Target;
            }
        } else if (var1 > var2) {
            this.shadowGradientLength1440 = var1 - 0.2F;
            if (Math.abs(var2 - this.shadowGradientLength1440) < 0.2F) {
                this.shadowGradientLength1440 = this.shadowGradientLength1440Target;
            }
        }

        if (this.shadowGradientLength1440 != var1) {
            this.setFastUpdateFlag();
        }

        var2 = this.shadowGradientLength1440;
        var1 = this.renderer.multiplier1440();

        float var3;
        for (var5 = 0; var5 < 4; ++var5) {
            this.mainRadius[var5] = this.baseRadii[var5] * this.unlockScaleAnims[var5].value();
            var9 = this.shadowInnerRadius;
            var13 = this.mainRadius;
            float var4 = var13[var5];
            var3 = var2 * var1 / 2.0F;
            var9[var5] = var4 - var3;
            this.shadowOuterRadius[var5] = var13[var5] + var3;
        }

        var3 = this.renderer.multiplier1440();

        for (var5 = 0; var5 < 4; ++var5) {
            var9 = this.highlightInnerRadius;
            var13 = this.mainRadius;
            var1 = var13[var5];
            var2 = var3 * 5.0F / 2.0F;
            var9[var5] = var1 - var2;
            this.highlightOuterRadius[var5] = var13[var5] + var2;
        }

        if (App.get().shadowTestSettings != null && App.get().shadowTestSettings.timeType != null) {
            if (App.get().shadowTestSettings.timeType == ShadowTestSettings.TimeType.DAYTIME || App.get().shadowTestSettings.timeType == ShadowTestSettings.TimeType.SUNRISE_SUNSET) {
                var8 = true;
            }
        } else {
            var8 = TimeUtil.isDaytime();
        }

        var1 = 0.5F;
        if (var8) {
            if (App.get().shadowTestSettings != null && App.get().shadowTestSettings.timeType != null) {
                if (App.get().shadowTestSettings.timeType != ShadowTestSettings.TimeType.DAYTIME) {
                    var1 = 0.99F;
                }
            } else {
                var1 = TimeUtil.daytimePercent();
            }

            this.ringRotation = MathUtil.lerp(var1, -1.5707964F, 1.5707964F);
            this.baseShadowRgb = ShadowColors.daytimeShadowRgb(var1, TimeUtil.daytimePercentOneHour() / 2.0F);
        } else {
            if (App.get().shadowTestSettings == null || App.get().shadowTestSettings.timeType == null) {
                var1 = TimeUtil.nighttimePercent();
            }

            this.ringRotation = MathUtil.lerp(var1, 1.5707964F, 4.712389F);
            this.baseShadowRgb = ShadowColors.nightimeShadowRgb(var1, TimeUtil.nighttimePercentOneHour() / 2.0F);
        }

        var2 = this.shadowAlphaMultiplier;
        var1 = this.shadowAlphaMultiplierTarget;
        if (var2 < var1) {
            this.shadowAlphaMultiplier = var2 + 0.003F;
            if (Math.abs(var1 - this.shadowAlphaMultiplier) < 0.003F) {
                this.shadowAlphaMultiplier = this.shadowAlphaMultiplierTarget;
            }
        } else if (var2 > var1) {
            this.shadowAlphaMultiplier = var2 - 0.003F;
            if (Math.abs(var1 - this.shadowAlphaMultiplier) < 0.003F) {
                this.shadowAlphaMultiplier = this.shadowAlphaMultiplierTarget;
            }
        }

        if (this.shadowAlphaMultiplier != var2) {
            this.setFastUpdateFlag();
        }

        this.baseShadowAlpha = this.shadowAlphaMultiplier * this.config.shadowAlpha;
        this.updateShadowColorArray();

    }

    public WallpaperColors getWallpaperColors() {
        return this.config.wallpaperColors;
    }

    public void onLockScreenUnlocked() {
        if (this.renderer.isPreview()) {
            return;
        }
        AnimFloat[] animFloatArray = this.unlockScaleAnims;
        int n = animFloatArray.length;
        for (int i = 0; i < n; ++i) {
            animFloatArray[i].start();
        }
    }

    public void onViewportSizeChanged() {
        Matrix.orthoM(this.matrix, 0, 0.0f, this.renderer.viewportWidth(), this.renderer.viewportHeight(), 0.0f, 0.0f, 1.0f);
        this.quad.setBasePositions(0.0f, 0.0f, this.renderer.viewportWidth(), this.renderer.viewportHeight());
        this.defaultCenter.x = (int) ((float) this.renderer.viewportWidth() * 0.5f);
        this.defaultCenter.y = this.renderer.isPortrait() ? (float) ((int) ((float) (this.renderer.viewportShortSide() * 16) / 9.0f * 0.2f)) : (float) ((int) ((float) this.renderer.viewportShortSide() * 0.5f));
        float f = !this.renderer.isPortrait() ? 0.75f : 1.0f;
        for (int i = 0; i < 4; ++i) {
            this.baseRadii[i] = (int) (BASE_RADII_1440[i] * this.renderer.multiplier1440() * f);
        }
        this.viewportSizeChangeFlag = true;
    }

    public void onVisibleAtLockScreen() {
        if (this.renderer.isPreview()) {
            return;
        }
        AnimFloat[] animFloatArray = this.unlockScaleAnims;
        int n = animFloatArray.length;
        for (int i = 0; i < n; ++i) {
            animFloatArray[i].reset();
        }
    }

    public void updateWeatherDependentParams() {
        final ShadowTestSettings shadowTestSettings = App.get().shadowTestSettings;
        final int n = 0;
        WeatherVo result;
        if (shadowTestSettings != null && App.get().shadowTestSettings.weatherIndex > -1) {
            result = new WeatherVo();
            result.conditions = new int[] { App.get().shadowTestSettings.weatherIndex };
        }
        else {
            result = ShadowWallpaperService.get().weatherMan().result();
        }
        if (App.get().shadowTestSettings != null && App.get().shadowTestSettings.useFastRandomWeather) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String sb = "simulated weather: " +
                            WeatherVo.conditionString(result.conditions[0]);
                    Toast.makeText((Context)App.get(), (CharSequence) sb, Toast.LENGTH_SHORT).show();
                }
            });
        }
        final int[] conditions = result.conditions;
        final int length = conditions.length;
        final float n2 = 0.0f;
        int i = 0;
        float max = 0.0f;
        while (i < length) {
            max = Math.max(max, Config.shadowAlphaMultiplierByWeatherCondition[conditions[i]]);
            ++i;
        }
        this.shadowAlphaMultiplierTarget = max;
        final int[] conditions2 = result.conditions;
        final int length2 = conditions2.length;
        float max2 = n2;
        for (int j = n; j < length2; ++j) {
            max2 = Math.max(max2, Config.shadowGradientLength1440ByWeatherCondition[conditions2[j]]);
        }
        this.shadowGradientLength1440Target = max2;
        final StringBuilder sb = new StringBuilder();
        sb.append("shadowGradientLength1440Target ");
        sb.append(this.shadowGradientLength1440Target);
        sb.append(" shadowAlphaMultiplierTarget ");
        sb.append(this.shadowAlphaMultiplierTarget);
        sb.append(" weather: ");
        sb.append(result.toString());
        L.v(sb.toString());

    }
}

