package com.jia.fantatic4.reunionnote.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jia on 2017/1/5.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
