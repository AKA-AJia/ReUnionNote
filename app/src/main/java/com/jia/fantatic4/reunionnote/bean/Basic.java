package com.jia.fantatic4.reunionnote.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jia on 2017/1/5.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public UpDate update;

    public class UpDate{
        @SerializedName("loc")
        public String updateTime;
    }
}
