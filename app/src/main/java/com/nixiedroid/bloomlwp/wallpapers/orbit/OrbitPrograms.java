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

public class OrbitPrograms extends RenderNode {
    private final PointF circlePos;
    private final Config config;
    private final PointF dotPos = new PointF();
    private final OrbitProgram1 program1;
    private final OrbitProgram2 program2;
    private final OrbitRenderer renderer;
    private float circleGradientTheta;
    private float circleOrbitRadius;
    private float circleRadius;
    private int circleTextureId;
    private float defaultCircleRadius;
    private float defaultDotRadius;
    private float defaultSquareRadius;
    private float dotOrbitRadius;
    private float dotRadius;
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
    float min = Float.MAX_VALUE;
    float min2 = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    float max2 = Float.MIN_VALUE;
    private void rot (float f4){
        MathUtil.rotate(this.scratchPt, f4, this.scratchPt);
        min = Math.min(min, this.scratchPt.x);
        min2 = Math.min(min2, this.scratchPt.y);
        max = Math.max(max, this.scratchPt.x);
        max2 = Math.max(max2, this.scratchPt.y);
    }

    private void calcSquareBoundingRect(float f, float f2, float f3, float f4, RectF rectF) {
        this.scratchPt.x = f3;
        this.scratchPt.y = f3;
        rot(f4);
        this.scratchPt.x = f3;
        this.scratchPt.y = -f3;
        rot(f4);
        this.scratchPt.x = -f3;
        this.scratchPt.y = f3;
        rot(f4);
        this.scratchPt.x = -f3;
        this.scratchPt.y = -f3;
        rot(f4);
        rectF.left = (float) Math.floor(min + f);
        rectF.right = (float) Math.floor(f + max);
        rectF.top = (float) Math.ceil(f2 + min2);
        rectF.bottom = (float) Math.ceil(f2 + max2);
    }

    private void updatePositions() {
        float f = (float) renderer.viewportWidth() / 2.0f;
        float f2 = (float) renderer.viewportHeight() / 2.0f;
        float f3 = this.renderer.time().hourHandPercent();
        MathUtil.pointOnCircle(f, f2, squareOrbitRadius, f3 * ((float) Math.PI * 2) * -1.0f + 1.5707964f, squarePos);
        squareTheta = renderer.time().secondHandPercent() * ((float) Math.PI * 2) * -1.0f;
        f3 = renderer.time().minuteHandPercent() * ((float) Math.PI * 2) * -1.0f;
        MathUtil.pointOnCircle(f, f2, circleOrbitRadius, f3 + 1.5707964f, circlePos);
        circleGradientTheta = config.doesCircleGradientRotate ? f3 : 1.5707964f;
        f3 = renderer.time().secondHandPercent() * ((float) Math.PI * 2) * -1.0f + 1.5707964f;
        if (config.doesDotOrbitCircle) {
            MathUtil.pointOnCircle(circlePos.x, circlePos.y, dotOrbitRadius, f3, dotPos);
        } else {
            MathUtil.pointOnCircle(f, f2, dotOrbitRadius, f3, dotPos);
        }
    }

    private void updateSizes() {
        float f;
        switch (renderer.phase()) {
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
        GLES20.glUseProgram(program1.programId());
        if (this.updateUniformsFlag) {
            GLES20.glUniformMatrix4fv(program1.matrixUniformId, 1, false, renderer.matrix(), 0);
            GLES20.glUniform1f(program1.widthUniformId, renderer.viewportWidth());
            GLES20.glUniform1f(program1.heightUniformId, renderer.viewportHeight());
            GLES20.glUniform4f(program1.dotColorUniformId, config.dotRgb[0], config.dotRgb[1], config.dotRgb[2], 1.0f);
            GLES20.glUniform4f(program1.bgColorUniformId, config.backgroundRgb[0], config.backgroundRgb[1], config.backgroundRgb[2], 1.0f);
            GLES20.glUniform4f(program1.dotCircleOverlapColorUniformId, config.dotCircleOverlapRgb[0], config.dotCircleOverlapRgb[1], config.dotCircleOverlapRgb[2], 1.0f);
            GLES20.glUniform4f(program1.squareCircleOverlapColorUniformId, config.squareCircleOverlapRgb[0], config.squareCircleOverlapRgb[1], config.squareCircleOverlapRgb[2], 1.0f);
            GLES20.glUniform4f(program1.dotSquareOverlapColorUniformId, config.dotSquareOverlapRgb[0], config.dotSquareOverlapRgb[1], config.dotSquareOverlapRgb[2], 1.0f);
            GLES20.glUniform4f(program1.allOverlapColorUniformId, config.allOverlapRgb[0], config.allOverlapRgb[1], config.allOverlapRgb[2], 1.0f);
            GLES20.glUniform1i(program1.textureUnitUniformId, 0);
        }
        GLES20.glUniform1f(this.program1.dotRadiusUniformId, dotRadius);
        GLES20.glUniform1f(this.program1.circleRadiusUniformId, circleRadius);
        GLES20.glUniform2f(this.program1.dotPosUniformId, dotPos.x, dotPos.y);
        n = this.program1.circlePosUniformId;
        PointF point = this.circlePos;
        GLES20.glUniform2f(n, point.x, point.y);
        GLES20.glUniform1f(this.program1.circleGradientThetaId, this.circleGradientTheta);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, this.circleTextureId);
        this.program1.quad.draw();
        GLES20.glUseProgram(this.program2.programId());
        this.calcSquareBoundingRect(this.squarePos.x, (float) this.renderer.viewportHeight() - this.squarePos.y, this.squareRadius, this.squareTheta, this.scratchRect);
        EasyQuad easyQuad = this.program2.quad;
        RectF rect;
        rect = this.scratchRect;
        easyQuad.setBasePositions(((RectF) rect).left, ((RectF) rect).top, ((RectF) rect).right, ((RectF) rect).bottom);
        GLES20.glUniform1f(this.program2.rectLeftUniformId, this.scratchRect.left);
        GLES20.glUniform1f(this.program2.rectRightUniformId, this.scratchRect.right);
        GLES20.glUniform1f(this.program2.rectTopUniformId, (float) this.renderer.viewportHeight() - this.scratchRect.bottom);
        GLES20.glUniform1f(this.program2.rectBottomUniformId, (float) this.renderer.viewportHeight() - this.scratchRect.top);
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
        program2.quad.draw();
        updateUniformsFlag = false;
    }

    @Override
    protected void doUpdate() {
        updatePositions();
        updateSizes();
    }

    public WallpaperColors getWallpaperColors() {
        return config.wallpaperColors;
    }

    public void onViewportSizeChanged() {
        updateParams();
        program1.quad.setBasePositions(0.0f, 0.0f, renderer.viewportWidth(), renderer.viewportHeight());
        updateUniformsFlag = true;
    }

    public void updateParams() {
        defaultDotRadius = config.dotRadius1440 / 1440.0f * (float) renderer.viewportShortSide();
        defaultSquareRadius = config.squareRadius1440 / 1440.0f * (float) renderer.viewportShortSide();
        defaultCircleRadius = config.circleRadius1440 / 1440.0f * (float) renderer.viewportShortSide();
        dotOrbitRadius = config.dotOrbitRadius1440 / 1440.0f * (float) renderer.viewportShortSide();
        squareOrbitRadius = config.squareOrbitRadius1440 / 1440.0f * (float) renderer.viewportShortSide();
        circleOrbitRadius = config.circleOrbitRadius1440 / 1440.0f * (float) renderer.viewportShortSide();
    }
}

