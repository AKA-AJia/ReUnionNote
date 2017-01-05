package com.jia.fantatic4.reunionnote.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jia on 2017/1/5.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
