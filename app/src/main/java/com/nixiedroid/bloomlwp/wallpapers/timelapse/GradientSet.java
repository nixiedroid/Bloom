/*
 * Decompiled with CFR 0.152.
 */
package com.nixiedroid.bloomlwp.wallpapers.timelapse;

import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.util.Terps;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradientSet {
    private static final String[] labels = new String[]{"NIGHT", "DAWN", "SUNRISE", "DAYTIME", "SUNSET", "DUSK"};
    private Range dawn;
    private Range dawnToSunrise;
    private Range daytime;
    private Range daytimeToSunset;
    private Range dusk;
    private Range duskToNight;
    private List<Gradient> list;
    private Range night1 = new Range();
    private Range night2;
    private Range nightToDawn = new Range();
    private Range sunrise;
    private Range sunriseToDaytime;
    private Range sunset;
    private Range sunsetToDusk;

    public GradientSet(Gradient gradient, Gradient gradient2, Gradient gradient3, Gradient gradient4, Gradient gradient5, Gradient gradient6) {
        this.dawn = new Range();
        this.dawnToSunrise = new Range();
        this.sunrise = new Range();
        this.sunriseToDaytime = new Range();
        this.daytime = new Range();
        this.daytimeToSunset = new Range();
        this.sunset = new Range();
        this.sunsetToDusk = new Range();
        this.dusk = new Range();
        this.duskToNight = new Range();
        this.night2 = new Range();
        this.list = new ArrayList<Gradient>(Arrays.asList(gradient, gradient2, gradient3, gradient4, gradient5, gradient6));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public float calcSlidingIndex(float f) {
        boolean bl = this.night1.contains(f);
        float f2 = 5.0f;
        if (bl) {
            return 0.0f;
        }
        if (this.nightToDawn.contains(f)) {
            return this.nightToDawn.normalize(f) + 0.0f;
        }
        if (this.dawn.contains(f)) {
            return 1.0f;
        }
        if (this.dawnToSunrise.contains(f)) {
            return this.dawnToSunrise.normalize(f) + 1.0f;
        }
        if (this.sunrise.contains(f)) {
            return 2.0f;
        }
        if (this.sunriseToDaytime.contains(f)) {
            return this.sunriseToDaytime.normalize(f) + 2.0f;
        }
        if (this.daytime.contains(f)) {
            return 3.0f;
        }
        if (this.daytimeToSunset.contains(f)) {
            return this.daytimeToSunset.normalize(f) + 3.0f;
        }
        if (this.sunset.contains(f)) {
            return 4.0f;
        }
        if (this.sunsetToDusk.contains(f)) {
            return this.sunsetToDusk.normalize(f) + 4.0f;
        }
        if (this.dusk.contains(f)) {
            return f2;
        }
        if (!this.duskToNight.contains(f)) return 0.0f;
        return 5.0f + this.duskToNight.normalize(f);
    }

    public void lerpUsingDayPercent(float f, boolean bl, Gradient gradient) {
        this.lerpUsingSlidingIndex(this.calcSlidingIndex(f), bl, gradient);
    }

    public void lerpUsingSlidingIndex(float f, boolean bl, Gradient gradient) {
        int n;
        float f2 = f;
        if (bl) {
            if ((f += GradientSetManager.oscillationOffset()) < 0.0f) {
                f2 = f + 6.0f;
            } else {
                f2 = f;
                if (f >= 6.0f) {
                    f2 = f - 6.0f;
                }
            }
        }
        int n2 = (int)f2;
        int n3 = n = (int)(f2 + 1.0f);
        if (n >= 6) {
            n3 = 0;
        }
        f = Terps.inOut().getInterpolation(f2 % 1.0f);
        Gradient.lerp(this.list.get(n2), this.list.get(n3), f, gradient);
    }

    public List<Gradient> list() {
        return this.list;
    }

    public void updateRanges() {
        float f = TimeUtil.sunriseDayPercent();
        float f2 = TimeUtil.sunsetDayPercent();
        Range range = this.night1;
        float f3 = f - 0.041666668f;
        range.set(0.0f, f3);
        range = this.nightToDawn;
        float f4 = f - 0.027777778f;
        range.set(f3, f4);
        range = this.dawn;
        f3 = f - 0.013888889f;
        range.set(f4, f3);
        this.dawnToSunrise.set(f3, f - 0.0f);
        range = this.sunrise;
        f4 = f + 0.013888889f;
        range.set(f + 0.0f, f4);
        range = this.sunriseToDaytime;
        range.set(f4, f += 0.027777778f);
        range = this.daytime;
        f4 = f2 - 0.027777778f;
        range.set(f, f4);
        range = this.daytimeToSunset;
        f = f2 - 0.013888889f;
        range.set(f4, f);
        this.sunset.set(f, f2 - 0.0f);
        range = this.sunsetToDusk;
        f4 = 0.013888889f + f2;
        range.set(0.0f + f2, f4);
        range = this.dusk;
        f = 0.027777778f + f2;
        range.set(f4, f);
        range = this.duskToNight;
        range.set(f, f2 += 0.041666668f);
        this.night2.set(f2, 1.01f);
    }

    public static class Range {
        float gte;
        float lt;

        public boolean contains(float f) {
            boolean bl = f >= this.gte && f < this.lt;
            return bl;
        }

        public float normalize(float f) {
            return MathUtil.normalize(f, this.gte, this.lt);
        }

        public void set(float f, float f2) {
            this.gte = f;
            this.lt = f2;
        }
    }
}

