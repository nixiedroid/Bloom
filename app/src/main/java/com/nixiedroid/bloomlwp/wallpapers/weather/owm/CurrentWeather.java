package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

import java.util.ArrayList;
import java.util.List;

public class CurrentWeather {

    private final List<Weather> weather = new ArrayList<>();
    public List<Weather> getWeather(){
        return weather;
    };

    public CurrentWeather() {
        weather.add(new Weather(8));
    }

    public static class Weather{
            private final int id;

            public Weather(int id) {
                this.id = id;
            }

            public int getId(){
               return id;
           };
        }
}
