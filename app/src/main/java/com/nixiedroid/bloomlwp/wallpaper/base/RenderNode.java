package com.nixiedroid.bloomlwp.wallpaper.base;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderNode {
    private final List<RenderNode> childNodes = new ArrayList<>();

    public final void addChildNode(RenderNode renderNode) {
        this.childNodes.add(renderNode);
    }

    protected abstract void doDraw();

    protected abstract void doUpdate();

    protected void draw() {
        this.doDraw();
        for (RenderNode childNode : this.childNodes) {
            childNode.draw();
        }
    }

    protected void update() {
        this.doUpdate();
        for (RenderNode childNode : this.childNodes) {
            childNode.update();
        }
    }
}

