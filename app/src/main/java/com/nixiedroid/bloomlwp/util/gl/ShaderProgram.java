package com.nixiedroid.bloomlwp.util.gl;

import com.nixiedroid.bloomlwp.wallpaper.base.RenderNode;

public abstract class ShaderProgram
extends RenderNode {
    protected int programId;

    protected ShaderProgram(int vertexShader, int fragmentShader) {
        this.programId = ShaderUtil.makeProgram(vertexShader, fragmentShader);
    }

}

