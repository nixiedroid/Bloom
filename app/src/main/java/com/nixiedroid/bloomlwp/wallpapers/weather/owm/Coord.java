package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

public class Coord {

    private double lon;
    private double lat;
    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public Coord(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }
}
