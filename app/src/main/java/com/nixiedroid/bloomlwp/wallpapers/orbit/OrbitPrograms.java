/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.orbit;

import android.app.WallpaperColors;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.util.gl.TextureUtil;
import com.nixiedroid.bloomlwp.wallpapers.base.EasyQuad;
import com.nixiedroid.bloomlwp.wallpapers.base.RenderNode;

public class OrbitPrograms
extends RenderNode {
    private float circleGradientTheta;
    private float circleOrbitRadius;
    private PointF circlePos;
    private float circleRadius;
    private int circleTextureId;
    private final Config config;
    private float defaultCircleRadius;
    private float defaultDotRadius;
    private float defaultSquareRadius;
    private float dotOrbitRadius;
    private PointF dotPos = new PointF();
    private float dotRadius;
    private final OrbitProgram1 program1;
    private final OrbitProgram2 program2;
    private final OrbitRenderer renderer;
    private PointF scratchPt;
    private RectF scratchRect;
    private float squareOrbitRadius;
    private PointF squarePos = new PointF();
    private float squareRadius;
    private float squareTheta;
    private boolean updateUniformsFlag;

    public OrbitPrograms(OrbitRenderer orbitRenderer) {
        this.circlePos = new PointF();
        this.scratchPt = new PointF();
        this.scratchRect = new RectF();
        L.d();
        this.renderer = orbitRenderer;
        this.program1 = new OrbitProgram1();
        this.program2 = new OrbitProgram2();
        this.config = OrbitWallpaperService.get().config();
        int n = this.renderer.displayShortSide() > 1080 ? this.config.circleTexture1440ResourceId : this.config.circleTexture1080ResourceId;
        this.circleTextureId = TextureUtil.loadTexture(App.get(), n, false);
    }

    private void calcSquareBoundingRect(float f, float f2, float f3, float f4, RectF rectF) {
        float f5;
        PointF pointF = this.scratchPt;
        pointF.x = f3;
        pointF.y = f3;
        MathUtil.rotate(pointF, f4, pointF);
        float f6 = Math.min(Float.MAX_VALUE, this.scratchPt.x);
        float f7 = Math.min(Float.MAX_VALUE, this.scratchPt.y);
        float f8 = Math.max(Float.MIN_VALUE, this.scratchPt.x);
        float f9 = Math.max(Float.MIN_VALUE, this.scratchPt.y);
        pointF = this.scratchPt;
        pointF.x = f3;
        pointF.y = f5 = -f3;
        MathUtil.rotate(pointF, f4, pointF);
        f6 = Math.min(f6, this.scratchPt.x);
        f7 = Math.min(f7, this.scratchPt.y);
        f8 = Math.max(f8, this.scratchPt.x);
        f9 = Math.max(f9, this.scratchPt.y);
        pointF = this.scratchPt;
        pointF.x = f5;
        pointF.y = f3;
        MathUtil.rotate(pointF, f4, pointF);
        f6 = Math.min(f6, this.scratchPt.x);
        f3 = Math.min(f7, this.scratchPt.y);
        f8 = Math.max(f8, this.scratchPt.x);
        f9 = Math.max(f9, this.scratchPt.y);
        pointF = this.scratchPt;
        pointF.x = f5;
        pointF.y = f5;
        MathUtil.rotate(pointF, f4, pointF);
        f4 = Math.min(f6, this.scratchPt.x);
        f3 = Math.min(f3, this.scratchPt.y);
        f5 = Math.max(f8, this.scratchPt.x);
        f8 = Math.max(f9, this.scratchPt.y);
        rectF.left = (float)Math.floor(f4 + f);
        rectF.right = (float)Math.floor(f + f5);
        rectF.top = (float)Math.ceil(f3 + f2);
        rectF.bottom = (float)Math.ceil(f2 + f8);
    }

    private void updatePositions() {
        float f = (float)this.renderer.viewportWidth() / 2.0f;
        float f2 = (float)this.renderer.viewportHeight() / 2.0f;
        float f3 = this.renderer.time().hourHandPercent();
        MathUtil.pointOnCircle(f, f2, this.squareOrbitRadius, f3 * ((float)Math.PI * 2) * -1.0f + 1.5707964f, this.squarePos);
        this.squareTheta = this.renderer.time().secondHandPercent() * ((float)Math.PI * 2) * -1.0f;
        f3 = this.renderer.time().minuteHandPercent() * ((float)Math.PI * 2) * -1.0f;
        MathUtil.pointOnCircle(f, f2, this.circleOrbitRadius, f3 + 1.5707964f, this.circlePos);
        this.circleGradientTheta = this.config.doesCircleGradientRotate ? f3 : 1.5707964f;
        f3 = this.renderer.time().secondHandPercent() * ((float)Math.PI * 2) * -1.0f + 1.5707964f;
        if (this.config.doesDotOrbitCircle) {
            PointF pointF = this.circlePos;
            MathUtil.pointOnCircle(pointF.x, pointF.y, this.dotOrbitRadius, f3, this.dotPos);
        } else {
            MathUtil.pointOnCircle(f, f2, this.dotOrbitRadius, f3, this.dotPos);
        }
    }

    private void updateSizes() {
        float f;
        switch (renderer.phase()){
            case DRAGGING:
            case POST_DRAG:
            case FLINGING:
                f = MathUtil.map(this.renderer.offsetRatio(), 1.0f, 0.0f, 0.9f, 1.0f);
                this.dotRadius = this.defaultDotRadius * f;
                this.squareRadius = this.defaultSquareRadius * f;
                this.circleRadius = this.defaultCircleRadius * f;
                break;
            default:
                f = MathUtil.lerp(this.renderer.offsetRatio(), 1.0f, 1.5f);
                this.dotRadius = this.defaultDotRadius * f;
                f = MathUtil.lerp(this.renderer.offsetRatio(), 1.0f, 1.1f);
                this.squareRadius = this.defaultSquareRadius * f;
                f = MathUtil.lerp(this.renderer.offsetRatio(), 1.0f, 0.9f);
                this.circleRadius = this.defaultCircleRadius * f;
                break;
        }
    }

    @Override
    protected void doDraw() {
        float[] rgb;
        int n;
        GLES20.glUseProgram(this.program1.programId());
        if (this.updateUniformsFlag) {
            GLES20.glUniformMatrix4fv(this.program1.matrixUniformId, 1, false, this.renderer.matrix(), 0);
            GLES20.glUniform1f(this.program1.widthUniformId, this.renderer.viewportWidth());
            GLES20.glUniform1f(this.program1.heightUniformId, this.renderer.viewportHeight());
            n = this.program1.dotColorUniformId;
            rgb = this.config.dotRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program1.bgColorUniformId;
            rgb = this.config.backgroundRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program1.dotCircleOverlapColorUniformId;
            rgb = this.config.dotCircleOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program1.squareCircleOverlapColorUniformId;
            rgb = this.config.squareCircleOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program1.dotSquareOverlapColorUniformId;
            rgb = this.config.dotSquareOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program1.allOverlapColorUniformId;
            rgb = this.config.allOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            GLES20.glUniform1i(this.program1.textureUnitUniformId, 0);
        }
        GLES20.glUniform1f(this.program1.dotRadiusUniformId, this.dotRadius);
        GLES20.glUniform1f(this.program1.circleRadiusUniformId, this.circleRadius);
        n = this.program1.dotPosUniformId;
        PointF point;
        point = this.dotPos;
        GLES20.glUniform2f(n, ((PointF) point).x, ((PointF) point).y);
        n = this.program1.circlePosUniformId;
        point = this.circlePos;
        GLES20.glUniform2f(n, ((PointF) point).x, ((PointF) point).y);
        GLES20.glUniform1f(this.program1.circleGradientThetaId, this.circleGradientTheta);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.circleTextureId);
        this.program1.quad.draw();
        GLES20.glUseProgram(this.program2.programId());
        this.calcSquareBoundingRect(this.squarePos.x, (float)this.renderer.viewportHeight() - this.squarePos.y, this.squareRadius, this.squareTheta, this.scratchRect);
        EasyQuad easyQuad = this.program2.quad;
        RectF rect;
        rect = this.scratchRect;
        easyQuad.setBasePositions(((RectF) rect).left, ((RectF) rect).top, ((RectF) rect).right, ((RectF) rect).bottom);
        GLES20.glUniform1f(this.program2.rectLeftUniformId, this.scratchRect.left);
        GLES20.glUniform1f(this.program2.rectRightUniformId, this.scratchRect.right);
        GLES20.glUniform1f(this.program2.rectTopUniformId, (float)this.renderer.viewportHeight() - this.scratchRect.bottom);
        GLES20.glUniform1f(this.program2.rectBottomUniformId, (float)this.renderer.viewportHeight() - this.scratchRect.top);
        if (this.updateUniformsFlag) {
            GLES20.glUniformMatrix4fv(this.program2.matrixUniformId, 1, false, this.renderer.matrix(), 0);
            n = this.program2.dotColorUniformId;
            rgb = this.config.dotRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program2.squareColorUniformId;
            rgb = this.config.squareRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program2.bgColorUniformId;
            rgb = this.config.backgroundRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program2.dotCircleOverlapColorUniformId;
            rgb = this.config.dotCircleOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program2.squareCircleOverlapColorUniformId;
            rgb = this.config.squareCircleOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program2.dotSquareOverlapColorUniformId;
            rgb = this.config.dotSquareOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            n = this.program2.allOverlapColorUniformId;
            rgb = this.config.allOverlapRgb;
            GLES20.glUniform4f(n, (float) rgb[0], (float) rgb[1], (float) rgb[2], 1.0f);
            GLES20.glUniform1i(this.program2.textureUnitUniformId, 0);
        }
        GLES20.glUniform1f(this.program2.dotRadiusUniformId, this.dotRadius);
        GLES20.glUniform1f(this.program2.squareRadiusUniformId, this.squareRadius);
        GLES20.glUniform1f(this.program2.circleRadiusUniformId, this.circleRadius);
        n = this.program2.dotPosUniformId;
        point = this.dotPos;
        GLES20.glUniform2f(n, ((PointF) point).x, ((PointF) point).y);
        n = this.program2.squarePosUniformId;
        point = this.squarePos;
        GLES20.glUniform2f(n, ((PointF) point).x, ((PointF) point).y);
        GLES20.glUniform1f(this.program2.squareThetaUniformId, this.squareTheta);
        n = this.program2.circlePosUniformId;
        point = this.circlePos;
        GLES20.glUniform2f(n, ((PointF) point).x, ((PointF) point).y);
        GLES20.glUniform1f(this.program2.circleGradientThetaId, this.circleGradientTheta);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.circleTextureId);
        this.program2.quad.draw();
        this.updateUniformsFlag = false;
    }

    @Override
    protected void doUpdate() {
        this.updatePositions();
        this.updateSizes();
    }

    public WallpaperColors getWallpaperColors() {
        return this.config.wallpaperColors;
    }

    public void onViewportSizeChanged() {
        this.updateParams();
        this.program1.quad.setBasePositions(0.0f, 0.0f, this.renderer.viewportWidth(), this.renderer.viewportHeight());
        this.updateUniformsFlag = true;
    }

    public void updateParams() {
        this.defaultDotRadius = this.config.dotRadius1440 / 1440.0f * (float)this.renderer.viewportShortSide();
        this.defaultSquareRadius = this.config.squareRadius1440 / 1440.0f * (float)this.renderer.viewportShortSide();
        this.defaultCircleRadius = this.config.circleRadius1440 / 1440.0f * (float)this.renderer.viewportShortSide();
        this.dotOrbitRadius = this.config.dotOrbitRadius1440 / 1440.0f * (float)this.renderer.viewportShortSide();
        this.squareOrbitRadius = this.config.squareOrbitRadius1440 / 1440.0f * (float)this.renderer.viewportShortSide();
        this.circleOrbitRadius = this.config.circleOrbitRadius1440 / 1440.0f * (float)this.renderer.viewportShortSide();
    }
}

