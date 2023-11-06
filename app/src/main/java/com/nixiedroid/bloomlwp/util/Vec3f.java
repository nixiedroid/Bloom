package com.nixiedroid.bloomlwp.util;

import org.jetbrains.annotations.NotNull;

public class Vec3f {

    public float x;
    public float y;
    public float z;
    public Vec3f(float x, float y, float z) {
        this.set(x, y, z);
    }

    public float length() {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vec3f vec3f) {
        this.x = vec3f.x;
        this.y = vec3f.y;
        this.z = vec3f.z;
    }

    @NotNull
    public String toString() {
        return "[Vec3f] " + this.x + ", " + this.y + ", " + this.z;
    }
}

