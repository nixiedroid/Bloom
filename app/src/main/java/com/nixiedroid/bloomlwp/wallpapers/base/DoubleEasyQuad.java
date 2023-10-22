package com.nixiedroid.bloomlwp.wallpapers.base;

public class DoubleEasyQuad {
    private final float dividerPercent;
    private final boolean isTopMain;
    private final EasyQuad quad1;
    private final EasyQuad quad2;

    public DoubleEasyQuad(boolean isTopMain, float dividerPercent) {
        this.isTopMain = isTopMain;
        this.dividerPercent = dividerPercent;
        this.quad1 = new EasyQuad();
        this.quad2 = new EasyQuad();
        this.quad1.textureCoords = new float[]{
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, dividerPercent,
                0.0f, 0.0f, 1.0f,
                dividerPercent, 0.0f, dividerPercent};
        this.quad2.textureCoords = new float[]{
                0.0f, dividerPercent, 1.0f,
                dividerPercent, 1.0f, 1.0f,
                0.0f, dividerPercent, 1.0f,
                1.0f, 0.0f, 1.0f};
    }

    public void draw() {
        this.quad1.draw();
        this.quad2.draw();
    }

    public void setBasePositions(float f, float f2, float f3, float f4) {
        float f5 = (f4 - f2) * this.dividerPercent + f2;
        this.quad1.setBasePositions(f, f2, f3, f5);
        this.quad2.setBasePositions(f, f5, f3, f4);
    }

    public void setColorsVerticalGradient(float[] fArray, float[] fArray2) {
        this.quad1.setUpperLeftRgb(fArray, 1.0f);
        this.quad1.setUpperRightRgb(fArray, 1.0f);
        this.quad2.setLowerLeftRgb(fArray2, 1.0f);
        this.quad2.setLowerRightRgb(fArray2, 1.0f);
        if (this.isTopMain) {
            this.quad1.setLowerLeftRgb(fArray2, 1.0f);
            this.quad1.setLowerRightRgb(fArray2, 1.0f);
            this.quad2.setUpperLeftRgb(fArray2, 1.0f);
            this.quad2.setUpperRightRgb(fArray2, 1.0f);
            return;
        }
        this.quad1.setLowerLeftRgb(fArray, 1.0f);
        this.quad1.setLowerRightRgb(fArray, 1.0f);
        this.quad2.setUpperLeftRgb(fArray, 1.0f);
        this.quad2.setUpperRightRgb(fArray, 1.0f);
    }

    public void setProgramIds(int positionAttributeId, int colorAttributeId, int textureCoordAttributeId) {
        this.quad1.setProgramIds(positionAttributeId, colorAttributeId, textureCoordAttributeId);
        this.quad2.setProgramIds(positionAttributeId, colorAttributeId, textureCoordAttributeId);
    }
}

