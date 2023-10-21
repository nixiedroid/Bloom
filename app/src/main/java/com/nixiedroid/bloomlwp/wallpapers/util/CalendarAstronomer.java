package com.nixiedroid.bloomlwp.wallpapers.util;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"SameParameterValue", "FieldCanBeLocal", "unused"})
class CalendarAstronomer {
    public static final SolarLongitude AUTUMN_EQUINOX;
    public static final MoonAge FIRST_QUARTER;
    public static final MoonAge FULL_MOON;
    public static final MoonAge LAST_QUARTER;
    public static final MoonAge NEW_MOON;
    public static final SolarLongitude SUMMER_SOLSTICE;
    public static final SolarLongitude VERNAL_EQUINOX;
    public static final SolarLongitude WINTER_SOLSTICE;
    private transient double eclipObliquity;
    private long fGmtOffset = 0L;
    private double fLatitude = 0.0;
    private double fLongitude = 0.0;
    private transient double julianCentury;
    private transient double julianDay = Double.MIN_VALUE;
    private transient double meanAnomalySun;
    private transient double moonEclipLong;
    private transient double moonLongitude;
    private transient Equatorial moonPosition = null;
    private transient double siderealT0;
    private transient double siderealTime;
    private transient double sunLongitude;
    private long time;

    static {
        VERNAL_EQUINOX = new SolarLongitude(0.0);
        SUMMER_SOLSTICE = new SolarLongitude(1.5707963267948966);
        AUTUMN_EQUINOX = new SolarLongitude(Math.PI);
        WINTER_SOLSTICE = new SolarLongitude(4.71238898038469);
        NEW_MOON = new MoonAge(0.0);
        FIRST_QUARTER = new MoonAge(1.5707963267948966);
        FULL_MOON = new MoonAge(Math.PI);
        LAST_QUARTER = new MoonAge(4.71238898038469);
    }

    public CalendarAstronomer() {
        this(System.currentTimeMillis());
    }

    public CalendarAstronomer(double longitude, double latitude) {
        this();
        this.fLongitude = normPI(longitude * (Math.PI / 180));
        this.fLatitude = normPI(latitude * (Math.PI / 180));
        this.fGmtOffset = (long)(this.fLongitude * 24.0 * 3600000.0 / (Math.PI * 2));
    }

    public CalendarAstronomer(long time) {
        this.julianCentury = Double.MIN_VALUE;
        this.sunLongitude = Double.MIN_VALUE;
        this.meanAnomalySun = Double.MIN_VALUE;
        this.moonLongitude = Double.MIN_VALUE;
        this.moonEclipLong = Double.MIN_VALUE;
        this.eclipObliquity = Double.MIN_VALUE;
        this.siderealT0 = Double.MIN_VALUE;
        this.siderealTime = Double.MIN_VALUE;
        this.time = time;
    }

    private void clearCache() {
        this.julianDay = Double.MIN_VALUE;
        this.julianCentury = Double.MIN_VALUE;
        this.sunLongitude = Double.MIN_VALUE;
        this.meanAnomalySun = Double.MIN_VALUE;
        this.moonLongitude = Double.MIN_VALUE;
        this.moonEclipLong = Double.MIN_VALUE;
        this.eclipObliquity = Double.MIN_VALUE;
        this.siderealTime = Double.MIN_VALUE;
        this.siderealT0 = Double.MIN_VALUE;
        this.moonPosition = null;
    }

    private double eclipticObliquity() {
        if (this.eclipObliquity == Double.MIN_VALUE) {
            double d = (this.getJulianDay() - 2451545.0) / 36525.0;
            this.eclipObliquity = 23.439292 - 0.013004166666666666 * d - 1.6666666666666665E-7 * d * d + 5.027777777777778E-7 * d * d * d;
            this.eclipObliquity *= Math.PI / 180;
        }
        return this.eclipObliquity;
    }

    private double getSiderealOffset() {
        if (this.siderealT0 == Double.MIN_VALUE) {
            double d = (Math.floor(this.getJulianDay() - 0.5) + 0.5 - 2451545.0) / 36525.0;
            this.siderealT0 = CalendarAstronomer.normalize(2400.051336 * d + 6.697374558 + 2.5862E-5 * d * d, 24.0);
        }
        return this.siderealT0;
    }

    private long lstToUT(double d) {
        d = CalendarAstronomer.normalize((d - this.getSiderealOffset()) * 0.9972695663, 24.0);
        long l = this.time;
        long l2 = this.fGmtOffset;
        return (l + l2) / 86400000L * 86400000L - l2 + (long)(d * 3600000.0);
    }

    private static double norm2PI(double d) {
        return normalize(d, Math.PI * 2);
    }

    private static double normPI(double d) {
        return normalize(d + Math.PI, Math.PI * 2) - Math.PI;
    }

    private static double normalize(double d, double d2) {
        return d - d2 * Math.floor(d / d2);
    }

    private long riseOrSet(CoordFunc coordFunc, boolean bl, double d, double d2, long l) {
        double d3;
        Equatorial equatorial;
        long l2;
        long l3;
        double d5 = Math.tan(this.fLatitude);
        int n = 0;
        do {
            equatorial = coordFunc.eval();
            d3 = Math.acos(-d5 * Math.tan(equatorial.declination));
            if (bl) {
                d3 = Math.PI * 2 - d3;
            }
            l3 = this.lstToUT((d3 + equatorial.ascension) * 24.0 / (Math.PI * 2));
            l2 = this.time;
            this.setTime(l3);
        } while (++n < 5 && Math.abs(l3 - l2) > l);
        double d4 = Math.cos(equatorial.declination);
        d3 = Math.acos(Math.sin(this.fLatitude) / d4);
        l3 = (long)(Math.asin(Math.sin(d / 2.0 + d2) / Math.sin(d3)) * 240.0 * 57.29577951308232 / d4 * 1000.0);
        l2 = this.time;
        l = l3;
        if (bl) {
            l = -l3;
        }
        return l2 + l;
    }

    private double trueAnomaly(double d, double d2) {
        double d3;
        double d4;
        double d5 = d;
        do {
            d4 = d5 - Math.sin(d5) * d2 - d;
            d5 = d3 = d5 - d4 / (1.0 - Math.cos(d5) * d2);
        } while (Math.abs(d4) > 1.0E-5);
        return Math.atan(Math.tan(d3 / 2.0) * Math.sqrt((d2 + 1.0) / (1.0 - d2))) * 2.0;
    }

    public final Equatorial eclipticToEquatorial(double d, double d2) {
        double d3 = this.eclipticObliquity();
        double d4 = Math.sin(d3);
        double d5 = Math.cos(d3);
        d3 = Math.sin(d);
        double d6 = Math.cos(d);
        double d7 = Math.sin(d2);
        d = Math.cos(d2);
        return new Equatorial(Math.atan2(d3 * d5 - Math.tan(d2) * d4, d6), Math.asin(d7 * d5 + d * d4 * d3));
    }

    public double getJulianDay() {
        if (this.julianDay == Double.MIN_VALUE) {
            this.julianDay = (double)(this.time + 210866760000000L) / 8.64E7;
        }
        return this.julianDay;
    }

    public double getSunLongitude() {
        if (this.sunLongitude == Double.MIN_VALUE) {
            double[] dArray = this.getSunLongitude(this.getJulianDay());
            this.sunLongitude = dArray[0];
            this.meanAnomalySun = dArray[1];
        }
        return this.sunLongitude;
    }

    double[] getSunLongitude(double d) {
        d = CalendarAstronomer.norm2PI(CalendarAstronomer.norm2PI((d - 2447891.5) * 0.017202791632524146) + 4.87650757829735 - 4.935239984568769);
        return new double[]{CalendarAstronomer.norm2PI(this.trueAnomaly(d, 0.016713) + 4.935239984568769), d};
    }

    public Equatorial getSunPosition() {
        return this.eclipticToEquatorial(this.getSunLongitude(), 0.0);
    }

    public long getSunRiseSet(boolean bl) {
        long l = this.time;
        long l2 = this.fGmtOffset;
        long l3 = (l + l2) / 86400000L;
        long l4 = bl ? -6L : 6L;
        this.setTime(l3 * 86400000L - l2 + 43200000L + l4 * 3600000L);
        l4 = this.riseOrSet(CalendarAstronomer.this::getSunPosition, bl, 0.009302604913129777, 0.009890199094634533, 5000L);
        this.setTime(l);
        return l4;
    }

    public void setTime(long l) {
        this.time = l;
        this.clearCache();
    }

    private interface CoordFunc {
        Equatorial eval();
    }

    public static final class Equatorial {
        public final double ascension;
        public final double declination;

        public Equatorial(double d, double d2) {
            this.ascension = d;
            this.declination = d2;
        }

        @NotNull
        public String toString() {
            return this.ascension * 57.29577951308232 + "," + this.declination * 57.29577951308232;
        }
    }

    private static class MoonAge {
        double value;

        MoonAge(double d) {
            this.value = d;
        }
    }

    private static class SolarLongitude {
        double value;

        SolarLongitude(double d) {
            this.value = d;
        }
    }
}

