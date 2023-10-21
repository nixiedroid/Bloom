package com.nixiedroid.bloomlwp.wallpapers.timelapse;

import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.util.Terps;
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradientSet {
    @SuppressWarnings("unused")
    private static final String[] labels = new String[]{"NIGHT", "DAWN", "SUNRISE", "DAYTIME", "SUNSET", "DUSK"};
    private final Range dawn;
    private final Range dawnToSunrise;
    private final Range daytime;
    private final Range daytimeToSunset;
    private final Range dusk;
    private final Range duskToNight;
    private final List<Gradient> list;
    private final Range night1;
    private final Range night2;
    private final Range nightToDawn;
    private final Range sunrise;
    private final Range sunriseToDaytime;
    private final Range sunset;
    private final Range sunsetToDusk;

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
        this.night1 = new Range();
        this.nightToDawn = new Range();
        this.list = new ArrayList<>(Arrays.asList(gradient, gradient2, gradient3, gradient4, gradient5, gradient6));
    }


    public float calcSlidingIndex(float f) {
        if (this.night1.contains(f)) {
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
            return 5.0f;
        }
        if (this.duskToNight.contains(f)) {
            return 5.0f + this.duskToNight.normalize(f);
        }
        return 0.0f;
    }

    public void lerpUsingDayPercent(float f, boolean bl, Gradient gradient) {
        this.lerpUsingSlidingIndex(this.calcSlidingIndex(f), bl, gradient);
    }

    public void lerpUsingSlidingIndex(float f, boolean z, Gradient gradient) {
        if (z) {
            f += GradientSetManager.oscillationOffset();
            if (f < 0.0f) {
                f += 6.0f;
            } else if (f >= 6.0f) {
                f -= 6.0f;
            }
        }
        int i = (int) (f + 1.0f);
        Gradient.lerp(this.list.get((int) f), this.list.get(i >= 6 ? 0 : i), Terps.inOut().getInterpolation(f % 1.0f), gradient);
    }

    public List<Gradient> list() {
        return this.list;
    }

    public void updateRanges() {
        float sunriseDayPercent = TimeUtil.sunriseDayPercent();
        float sunsetDayPercent = TimeUtil.sunsetDayPercent();
        float f = sunriseDayPercent - 0.041666668f;
        this.night1.set(0.0f, f);
        float f2 = sunriseDayPercent - 0.027777778f;
        this.nightToDawn.set(f, f2);
        float f3 = sunriseDayPercent - 0.013888889f;
        this.dawn.set(f2, f3);
        this.dawnToSunrise.set(f3, sunriseDayPercent - 0.0f);
        float f4 = sunriseDayPercent + 0.013888889f;
        this.sunrise.set(sunriseDayPercent + 0.0f, f4);
        float f5 = sunriseDayPercent + 0.027777778f;
        this.sunriseToDaytime.set(f4, f5);
        float f6 = sunsetDayPercent - 0.027777778f;
        this.daytime.set(f5, f6);
        float f7 = sunsetDayPercent - 0.013888889f;
        this.daytimeToSunset.set(f6, f7);
        this.sunset.set(f7, sunsetDayPercent - 0.0f);
        float f8 = 0.013888889f + sunsetDayPercent;
        this.sunsetToDusk.set(0.0f + sunsetDayPercent, f8);
        float f9 = 0.027777778f + sunsetDayPercent;
        this.dusk.set(f8, f9);
        float f10 = sunsetDayPercent + 0.041666668f;
        this.duskToNight.set(f9, f10);
        this.night2.set(f10, 1.01f);
    }

    public static class Range {
        float gte;
        float lt;

        public boolean contains(float f) {
            return f >= this.gte && f < this.lt;
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

