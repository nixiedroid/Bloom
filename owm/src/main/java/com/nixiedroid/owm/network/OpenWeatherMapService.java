package com.nixiedroid.owm.network;

import com.nixiedroid.owm.model.currentweather.CurrentWeather;
import com.nixiedroid.owm.model.threehourforecast.ThreeHourForecast;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Kwabena Berko on 7/25/2017.
 */

public interface OpenWeatherMapService {

    String CURRENT = "/data/2.5/weather";
    String FORECAST = "/data/2.5/forecast";

    //Current Weather Endpoints start
    @GET(CURRENT)
    Call<CurrentWeather> getCurrentWeatherByCityName(@QueryMap Map<String, String> options);

    @GET(CURRENT)
    Call<CurrentWeather> getCurrentWeatherByCityID(@QueryMap Map<String, String> options);

    @GET(CURRENT)
    Call<CurrentWeather> getCurrentWeatherByGeoCoordinates(@QueryMap Map<String, String> options);

    @GET(CURRENT)
    Call<CurrentWeather> getCurrentWeatherByZipCode(@QueryMap Map<String, String> options);

    //Current Weather Endpoints end

    //Three hour forecast endpoints start
    @GET(FORECAST)
    Call<ThreeHourForecast> getThreeHourForecastByCityName(@QueryMap Map<String, String> options);

    @GET(FORECAST)
    Call<ThreeHourForecast> getThreeHourForecastByCityID(@QueryMap Map<String, String> options);

    @GET(FORECAST)
    Call<ThreeHourForecast> getThreeHourForecastByGeoCoordinates(@QueryMap Map<String, String> options);

    @GET(FORECAST)
    Call<ThreeHourForecast> getThreeHourForecastByZipCode(@QueryMap Map<String, String> options);
}
