package com.jia.fantatic4.reunionnote.utils;

/**
 * Created by jia on 2016/12/31.
 */


import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * okhttp工具类
 */
public class HttpUtil {

    public static void sendOkHttpRequest(String url,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
}
