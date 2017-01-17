package com.jia.fantatic4.reunionnote.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jia.fantatic4.reunionnote.R;
import com.jia.fantatic4.reunionnote.bean.Forecast;
import com.jia.fantatic4.reunionnote.bean.Weather;
import com.jia.fantatic4.reunionnote.constant.Constant;
import com.jia.fantatic4.reunionnote.service.AutoUpdateService;
import com.jia.fantatic4.reunionnote.utils.HttpUtil;
import com.jia.fantatic4.reunionnote.utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jia on 2017/1/5.
 */

public class WeatherDetailsActivity extends AppCompatActivity {


    private ScrollView scrollView;
    private LinearLayout ll_forecast;
    private TextView tv_title;
    private TextView tv_aqi;
    private TextView tv_pm;
    private TextView tv_comfort;
    private TextView tv_carWash;
    private TextView tv_sport;
    private TextView tv_update_time;
    private TextView tv_now_tmp;
    private TextView tv_now_status;
    private ImageView iv_weather_bg;
    public SwipeRefreshLayout swipeRefreshLayout;

    public DrawerLayout drawerLayout;
    private Button btn_home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏跟背景图融合
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather_detail);
        initViews();
        initSpData();

    }



    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btn_home = (Button) findViewById(R.id.btn_weather_home);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sw_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        scrollView = (ScrollView) findViewById(R.id.sc_weather);
        ll_forecast = (LinearLayout) findViewById(R.id.ll_forecast_weather);
        tv_title = (TextView) findViewById(R.id.tv_weather_title);
        tv_aqi = (TextView) findViewById(R.id.tv_aqi_date);
        tv_pm = (TextView) findViewById(R.id.tv_pm25_date);
        tv_comfort = (TextView) findViewById(R.id.tv_suggestion_comfort);
        tv_carWash = (TextView) findViewById(R.id.tv_suggestion_carwash);
        tv_sport = (TextView) findViewById(R.id.tv_suggestion_sport);
        tv_update_time = (TextView) findViewById(R.id.tv_weather_current_time);
        tv_now_tmp = (TextView) findViewById(R.id.tv_now_tmp);
        tv_now_status = (TextView) findViewById(R.id.tv_now_status);
        iv_weather_bg = (ImageView) findViewById(R.id.iv_weather_bg);

    }

    /**
     * 优先查询sp中是否有缓存
     */
    private void initSpData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherData = prefs.getString("weather", null);
        final String weatherId;
        //有缓存
        if (weatherData != null) {
            Weather weather = Utility.handleWeatherResponse(weatherData);
            weatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存
            weatherId = getIntent().getStringExtra("weather_id");
            scrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }


        //显示背景图片
        String bing_pic = prefs.getString("bing_pic", null);
        if (bing_pic != null) {
            Glide.with(this).load(bing_pic).into(iv_weather_bg);
        } else {
            loadBingPic();
        }

        initEvent(weatherId);
    }

    /**
     * 初始化事件
     */
    private void initEvent(final String weatherId) {

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        //点击打开左侧栏
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 请求背景图片数据
     */
    private void loadBingPic() {
        HttpUtil.sendOkHttpRequest(Constant.WEATHER_IMG_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String pic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherDetailsActivity.this).edit();
                editor.putString("bing_pic", pic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherDetailsActivity.this).load(pic)
                                .into(iv_weather_bg);
                    }
                });
            }
        });
    }

    /**
     * 请求天气数据
     * @param weather_id 天气ID
     */
    public void requestWeather(String weather_id) {
        String weatherUrl = Constant.WEATHER_BASE_URL + weather_id + Constant.WEATHER_KEY;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherDetailsActivity.this,
                                "获取天气信息失败...", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String weatherData = response.body().string();
                final Weather weatherInfo = Utility.handleWeatherResponse(weatherData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherInfo != null && "ok".equals(weatherInfo.status)) {
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherDetailsActivity.this)
                                            .edit();
                            editor.putString("weather", weatherData);
                            editor.apply();
                            showWeatherInfo(weatherInfo);
                        }else {
                            Toast.makeText(WeatherDetailsActivity.this,"加载天气数据失败...",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });

        loadBingPic();
    }

    /**
     * 显示天气数据到界面
     *
     * @param weatherInfo 天气bean
     */
    private void showWeatherInfo(Weather weatherInfo) {
        if (weatherInfo!=null&&"ok".equals(weatherInfo.status)){
            String cityName = weatherInfo.basic.cityName;
            String updateTime = weatherInfo.basic.update.updateTime.split(" ")[1];
            String tmp = weatherInfo.now.temperature + "°C";
            String now_status = weatherInfo.now.more.info;
            tv_title.setText(cityName);
            tv_update_time.setText(updateTime);
            tv_now_tmp.setText(tmp);
            tv_now_status.setText(now_status);

            ll_forecast.removeAllViews();
            for (Forecast forecast : weatherInfo.forecastList) {
                View view = LayoutInflater
                        .from(this).inflate(R.layout.forecast_weather_item, ll_forecast, false);
                TextView tv_date = (TextView) view.findViewById(R.id.tv_forecast_time);
                TextView tv_stutas = (TextView) view.findViewById(R.id.tv_forecast_status);
                TextView tv_max = (TextView) view.findViewById(R.id.tv_forecast_max_tmp);
                TextView tv_min = (TextView) view.findViewById(R.id.tv_forecast_min_tmp);

                tv_date.setText(forecast.date);
                tv_stutas.setText(forecast.more.info);
                tv_max.setText(forecast.temperature.max);
                tv_min.setText(forecast.temperature.min);

                ll_forecast.addView(view);
            }

            if (weatherInfo.aqi != null) {
                tv_aqi.setText(weatherInfo.aqi.city.aqi);
                tv_pm.setText(weatherInfo.aqi.city.pm25);
            }

            String comfort = "舒适度：" + weatherInfo.suggestion.comfort.info;
            String carWash = "洗车指数：" + weatherInfo.suggestion.carWash.info;
            String sport = "运动建议：" + weatherInfo.suggestion.sport.info;

            tv_comfort.setText(comfort);
            tv_carWash.setText(carWash);
            tv_sport.setText(sport);

            scrollView.setVisibility(View.VISIBLE);

            //开启服务
            Intent intent=new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else {
            Toast.makeText(this,"更新天气数据失败...",Toast.LENGTH_SHORT).show();
        }

    }
}
