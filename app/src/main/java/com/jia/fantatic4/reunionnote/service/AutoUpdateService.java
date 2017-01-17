package com.jia.fantatic4.reunionnote.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.jia.fantatic4.reunionnote.bean.Weather;
import com.jia.fantatic4.reunionnote.constant.Constant;
import com.jia.fantatic4.reunionnote.utils.HttpUtil;
import com.jia.fantatic4.reunionnote.utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();

        //?????
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//8小时
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新必应图片
     */
    private void updateBingPic() {
        HttpUtil.sendOkHttpRequest(Constant.WEATHER_IMG_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }

    /**
     * 更新天气数据
     */
    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存时直接解析天气
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = Constant.WEATHER_BASE_URL + weatherId + Constant.WEATHER_KEY;

            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherText = response.body().string();
                    Weather weatherData = Utility.handleWeatherResponse(weatherText);
                    if (weatherData != null && weatherData.status.equals("ok")) {
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",weatherText);
                        editor.apply();
                    }
                }
            });
        }
    }
}
