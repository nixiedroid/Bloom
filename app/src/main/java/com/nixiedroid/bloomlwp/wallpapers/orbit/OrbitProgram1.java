/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.orbit;

import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.gl.ShaderProgram;
import com.nixiedroid.bloomlwp.wallpapers.base.EasyQuad;

public class OrbitProgram1
extends ShaderProgram {
    public final int allOverlapColorUniformId;
    public final int bgColorUniformId;
    public final int circleGradientThetaId;
    public final int circlePosUniformId;
    public final int circleRadiusUniformId;
    public final int colorAttributeId;
    public final int dotCircleOverlapColorUniformId;
    public final int dotColorUniformId;
    public final int dotPosUniformId;
    public final int dotRadiusUniformId;
    public final int dotSquareOverlapColorUniformId;
    public final int heightUniformId;
    public final int matrixUniformId;
    public final int positionAttributeId;
    public final EasyQuad quad;
    public final int squareCircleOverlapColorUniformId;
    public final int textureCoordsAttributeId;
    public final int textureUnitUniformId;
    public final int widthUniformId;

    public OrbitProgram1() {
        super(App.get(), R.raw.orbit_vertex_shader, R.raw.orbit_fragment_shader_1);
        this.matrixUniformId = GLES20.glGetUniformLocation(this.programId, "uMatrix");
        this.positionAttributeId = GLES20.glGetAttribLocation(this.programId, "aPosition");
        this.colorAttributeId = GLES20.glGetAttribLocation(this.programId, "aColor");
        this.textureCoordsAttributeId = GLES20.glGetAttribLocation(this.programId, "aTextureCoordinates");
        this.widthUniformId = GLES20.glGetUniformLocation(this.programId, "width");
        this.heightUniformId = GLES20.glGetUniformLocation(this.programId, "height");
        this.dotPosUniformId = GLES20.glGetUniformLocation(this.programId, "dotPos");
        this.dotRadiusUniformId = GLES20.glGetUniformLocation(this.programId, "dotRadius");
        this.dotColorUniformId = GLES20.glGetUniformLocation(this.programId, "dotColor");
        this.circlePosUniformId = GLES20.glGetUniformLocation(this.programId, "circlePos");
        this.circleRadiusUniformId = GLES20.glGetUniformLocation(this.programId, "circleRadius");
        this.circleGradientThetaId = GLES20.glGetUniformLocation(this.programId, "circleGradientTheta");
        this.bgColorUniformId = GLES20.glGetUniformLocation(this.programId, "bgColor");
        this.dotCircleOverlapColorUniformId = GLES20.glGetUniformLocation(this.programId, "dotCircleOverlapColor");
        this.squareCircleOverlapColorUniformId = GLES20.glGetUniformLocation(this.programId, "squareCircleOverlapColor");
        this.dotSquareOverlapColorUniformId = GLES20.glGetUniformLocation(this.programId, "dotSquareOverlapColor");
        this.allOverlapColorUniformId = GLES20.glGetUniformLocation(this.programId, "allOverlapColor");
        this.textureUnitUniformId = GLES20.glGetUniformLocation(this.programId, "textureUnit");
        this.quad = new EasyQuad();
        this.quad.setProgramIds(this.positionAttributeId, this.colorAttributeId, this.textureCoordsAttributeId);
    }

    @Override
    protected void doDraw() {
    }

    @Override
    protected void doUpdate() {
    }
}
