package com.nixiedroid.bloomlwp.wallpaper.bloom;

import com.nixiedroid.bloomlwp.util.MathUtil;
import com.nixiedroid.bloomlwp.util.Terps;
import com.nixiedroid.bloomlwp.weather.TimeUtil;
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


    public float calcSlidingIndex(float timeValue) {
        if (this.night1.contains(timeValue)) {
            return 0.0f;
        }
        if (this.nightToDawn.contains(timeValue)) {
            return this.nightToDawn.normalize(timeValue) + 0.0f;
        }
        if (this.dawn.contains(timeValue)) {
            return 1.0f;
        }
        if (this.dawnToSunrise.contains(timeValue)) {
            return this.dawnToSunrise.normalize(timeValue) + 1.0f;
        }
        if (this.sunrise.contains(timeValue)) {
            return 2.0f;
        }
        if (this.sunriseToDaytime.contains(timeValue)) {
            return this.sunriseToDaytime.normalize(timeValue) + 2.0f;
        }
        if (this.daytime.contains(timeValue)) {
            return 3.0f;
        }
        if (this.daytimeToSunset.contains(timeValue)) {
            return this.daytimeToSunset.normalize(timeValue) + 3.0f;
        }
        if (this.sunset.contains(timeValue)) {
            return 4.0f;
        }
        if (this.sunsetToDusk.contains(timeValue)) {
            return this.sunsetToDusk.normalize(timeValue) + 4.0f;
        }
        if (this.dusk.contains(timeValue)) {
            return 5.0f;
        }
        if (this.duskToNight.contains(timeValue)) {
            return 5.0f + this.duskToNight.normalize(timeValue);
        }
        return 0.0f;
    }

    public void lerpUsingDayPercent(float f, Gradient gradient) {
        this.lerpUsingSlidingIndex(this.calcSlidingIndex(f),  gradient);
    }

    public void lerpUsingSlidingIndex(float f, Gradient gradient) {
        f += GradientSetManager.oscillationOffset();
        if (f < 0.0f) {
            f += 6.0f;
        } else if (f >= 6.0f) {
            f -= 6.0f;
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
        float night = sunriseDayPercent - 0.041666668f;
        this.night1.set(0.0f, night);
        float nightToDawn = sunriseDayPercent - 0.027777778f;
        this.nightToDawn.set(night, nightToDawn);
        float dawn = sunriseDayPercent - 0.013888889f;
        this.dawn.set(nightToDawn, dawn);
        this.dawnToSunrise.set(dawn, sunriseDayPercent - 0.0f);
        float sunrise = sunriseDayPercent + 0.013888889f;
        this.sunrise.set(sunriseDayPercent + 0.0f, sunrise);
        float sunriseToDaytime = sunriseDayPercent + 0.027777778f;
        this.sunriseToDaytime.set(sunrise, sunriseToDaytime);
        float daytime = sunsetDayPercent - 0.027777778f;
        this.daytime.set(sunriseToDaytime, daytime);
        float daytimeToSunset = sunsetDayPercent - 0.013888889f;
        this.daytimeToSunset.set(daytime, daytimeToSunset);
        this.sunset.set(daytimeToSunset, sunsetDayPercent - 0.0f);
        float sunsetToDusk = 0.013888889f + sunsetDayPercent;
        this.sunsetToDusk.set(0.0f + sunsetDayPercent, sunsetToDusk);
        float dusk = 0.027777778f + sunsetDayPercent;
        this.dusk.set(sunsetToDusk, dusk);
        float duskToNight = sunsetDayPercent + 0.041666668f;
        this.duskToNight.set(dusk, duskToNight);
        this.night2.set(duskToNight, 1.01f);
    }

    public static class Range {
        float start;
        float end;

        public boolean contains(float time) {
            return time >= this.start && time < this.end;
        }

        public float normalize(float f) {
            return MathUtil.normalize(f, this.start, this.end);
        }

        public void set(float start, float end) {
            this.start = start;
            this.end = end;
        }
    }
}

