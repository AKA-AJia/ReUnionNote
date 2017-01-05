package com.jia.fantatic4.reunionnote.utils;

/**
 * Created by jia on 2016/12/31.
 */

import android.text.TextUtils;

import com.google.gson.Gson;
import com.jia.fantatic4.reunionnote.bean.Weather;
import com.jia.fantatic4.reunionnote.db.CityDB;
import com.jia.fantatic4.reunionnote.db.CountyDB;
import com.jia.fantatic4.reunionnote.db.ProvinceDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * json数据解析工具类
 */

public class Utility {

    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces=new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    ProvinceDB province=new ProvinceDB();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity=new JSONArray(response);
                for (int i=0;i<allCity.length();i++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    CityDB city=new CityDB();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties=new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    CountyDB county=new CountyDB();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            if(response != null && response.startsWith("\ufeff"))
            {
                response =  response.substring(1);
            }
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
