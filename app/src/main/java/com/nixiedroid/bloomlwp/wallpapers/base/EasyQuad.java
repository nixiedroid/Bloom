package com.nixiedroid.bloomlwp.wallpapers.base;

import android.opengl.GLES20;
import com.nixiedroid.bloomlwp.util.gl.VertexArray;


public class EasyQuad
extends Quad {
    protected VertexArray colorArray;
    protected int colorAttributeId;
    protected float[] colors = new float[24];
    protected boolean colorsDirty;
    protected boolean hasSetAttributesIds;
    protected VertexArray positionArray;
    protected int positionAttributeId;
    protected boolean positionsDirty;
    protected boolean texCoordsDirty;
    protected VertexArray textureCoordArray;
    protected int textureCoordAttributeId;

    public EasyQuad() {
        super(1.0f, 1.0f, Quad.Origin.CENTER);
        this.ctor2();
    }

    private void ctor2() {
        this.positionArray = new VertexArray(this.transformedPositions);
        this.colorArray = new VertexArray(this.colors);
        this.textureCoordArray = new VertexArray(this.textureCoords);
        this.texCoordsDirty = true;
    }

    @Override
    public void applyMatrix() {
        super.applyMatrix();
        this.positionsDirty = true;
    }

    public void draw() {
        if (this.hasSetAttributesIds) {
            if (this.positionsDirty) {
                this.positionArray.updateBuffer(this.transformedPositions, 0, this.transformedPositions.length);
                this.positionsDirty = false;
            }
            if (this.colorsDirty) {
                this.writeColors(this.colors, 0);
                this.colorArray.updateBuffer(this.colors, 0, this.colors.length);
                this.colorsDirty = false;
            }
            if (this.texCoordsDirty) {
                this.textureCoordArray.updateBuffer(this.textureCoords, 0, this.textureCoords.length);
                this.texCoordsDirty = false;
            }
            this.positionArray.setVertexAttribPointer(0, this.positionAttributeId, 3, 12);
            this.colorArray.setVertexAttribPointer(0, this.colorAttributeId, 4, 16);
            this.textureCoordArray.setVertexAttribPointer(0, this.textureCoordAttributeId, 2, 8);
            GLES20.glDrawArrays(4, 0, 6);
            return;
        }
        throw new Error("must setProgramIds()");
    }

    @Override
    public void setBasePositions(float f, float f2, float f3, float f4) {
        super.setBasePositions(f, f2, f3, f4);
        this.positionsDirty = true;
    }

    @Override
    public void setLowerLeftRgb(float f, float f2, float f3, float f4) {
        super.setLowerLeftRgb(f, f2, f3, f4);
        this.colorsDirty = true;
    }

    @Override
    public void setLowerRightRgb(float f, float f2, float f3, float f4) {
        super.setLowerRightRgb(f, f2, f3, f4);
        this.colorsDirty = true;
    }

    public void setProgramIds(int n, int n2, int n3) {
        this.positionAttributeId = n;
        this.colorAttributeId = n2;
        this.textureCoordAttributeId = n3;
        this.hasSetAttributesIds = true;
    }

    @Override
    public void setUpperLeftRgb(float f, float f2, float f3, float f4) {
        super.setUpperLeftRgb(f, f2, f3, f4);
        this.colorsDirty = true;
    }

    @Override
    public void setUpperRightRgb(float f, float f2, float f3, float f4) {
        super.setUpperRightRgb(f, f2, f3, f4);
        this.colorsDirty = true;
    }
}

