package com.nixiedroid.bloomlwp.util;

public class Vec3f {
    private static final Vec3f scratch = new Vec3f();
    public float x;
    public float y;
    public float z;

    public Vec3f() {
    }

    public Vec3f(float f, float f2, float f3) {
        this.set(f, f2, f3);
    }

    public static float distance(Vec3f vec3f, Vec3f vec3f2) {
        Vec3f.subtract(vec3f, vec3f2, scratch);
        return scratch.length();
    }

    public static boolean equals(Vec3f vec3f, Vec3f vec3f2) {
        return vec3f.x == vec3f2.x && vec3f.y == vec3f2.y && vec3f.z == vec3f2.z;
    }

    public static void subtract(Vec3f vec3f, Vec3f vec3f2, Vec3f vec3f3) {
        vec3f3.x = vec3f.x - vec3f2.x;
        vec3f3.y = vec3f.y - vec3f2.y;
        vec3f3.z = vec3f.z - vec3f2.z;
    }

    public float length() {
        float f = this.x;
        float f2 = this.y;
        float f3 = this.z;
        return (float) Math.sqrt(f * f + f2 * f2 + f3 * f3);
    }

    public void lerp(float f, Vec3f vec3f) {
        this.x = MathUtil.lerp(f, this.x, vec3f.x);
        this.y = MathUtil.lerp(f, this.y, vec3f.y);
        this.z = MathUtil.lerp(f, this.z, vec3f.z);
    }

    public void set(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.z = f3;
    }

    public void set(Vec3f vec3f) {
        this.x = vec3f.x;
        this.y = vec3f.y;
        this.z = vec3f.z;
    }

    public String toString() {
        return "[Vec3f] " + this.x + ", " + this.y + ", " + this.z;
    }
}

