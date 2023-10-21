package com.nixiedroid.bloomlwp.util;

import org.jetbrains.annotations.NotNull;

public class Vec3f {
    private static final Vec3f temp = new Vec3f();
    public float x;
    public float y;
    public float z;

    public Vec3f() {
    }

    public Vec3f(float x, float y, float z) {
        this.set(x, y, z);
    }

    public static float distance(Vec3f first, Vec3f second) {
        Vec3f.subtract(first, second, temp);
        return temp.length();
    }

    public static boolean equals(Vec3f first, Vec3f second) {
        return first.x == second.x && first.y == second.y && first.z == second.z;
    }

    public static void subtract(Vec3f first, Vec3f second, Vec3f output) {
        output.x = first.x - second.x;
        output.y = first.y - second.y;
        output.z = first.z - second.z;
    }

    public float length() {
        float x = this.x;
        float y = this.y;
        float z = this.z;
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void lerp(float start, Vec3f vec3f) {
        this.x = MathUtil.lerp(start, this.x, vec3f.x);
        this.y = MathUtil.lerp(start, this.y, vec3f.y);
        this.z = MathUtil.lerp(start, this.z, vec3f.z);
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

