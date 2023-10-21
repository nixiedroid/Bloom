package com.nixiedroid.bloomlwp.util.gl;

import android.content.Context;
import com.nixiedroid.bloomlwp.wallpapers.base.RenderNode;

public abstract class ShaderProgram
extends RenderNode {
    protected int programId;

    protected ShaderProgram(Context context, int vertexShader, int fragmentShader) {
        this.programId = ShaderUtil.makeProgram(context, vertexShader, fragmentShader);
    }

    public int programId() {
        return this.programId;
    }
}
