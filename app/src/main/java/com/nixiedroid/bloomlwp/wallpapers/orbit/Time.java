package com.nixiedroid.bloomlwp.wallpapers.orbit;

public class Time {
    private float dayPercent;

    public float hourHandPercent() {
        return this.dayPercent / 0.5f % 1.0f;
    }

    public float minuteHandPercent() {
        return this.dayPercent / 0.041666668f % 1.0f;
    }

    public float secondHandPercent() {
        return this.minuteHandPercent() / 0.016666668f % 1.0f;
    }

    public void setDayPercent(float f) {
        this.dayPercent = f;
    }
}

